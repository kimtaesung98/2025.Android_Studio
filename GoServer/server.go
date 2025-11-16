package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strconv"
	"strings" // ⭐️ [신규] "Bearer " 접두사 제거
	"time"

	_ "github.com/glebarez/go-sqlite" // (22단계 Plan B)

	"github.com/golang-jwt/jwt/v5"
	"golang.org/x/crypto/bcrypt"
)

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

type FeedItem struct {
	ID                  string  `json:"id"`
	UserName            string  `json:"user_name"`
	UserProfileImageUrl *string `json:"user_profile_image_url"`
	PostImageUrl        *string `json:"post_image_url"`
	Content             string  `json:"content"`
	LikesCount          *int    `json:"likes_count"`
	IsLiked             bool    `json:"is_liked"` // ⭐️ [신규] '좋아요' 여부 (JOIN 결과)
}
type DeliveryItem struct {
	ID                     string  `json:"id"`
	StoreName              string  `json:"store_name"`
	StoreImageUrl          *string `json:"store_image_url"`           // ⭐️ FIX: sql.NullString -> *string
	EstimatedTimeInMinutes *int    `json:"estimated_time_in_minutes"` // ⭐️ FIX: sql.NullInt64 -> *int
	Status                 string  `json:"status"`
}
type ShortsItem struct {
	ID        string  `json:"id"`
	StoreName string  `json:"store_name"`
	StoreID   string  `json:"store_id"`
	VideoURL  *string `json:"video_url"` // ⭐️ FIX: sql.NullString -> *string
}

// ⭐️ [신규] User 구조체 (DB 저장용)
type User struct {
	ID             int    `json:"id"`
	Email          string `json:"email"`
	HashedPassword string `json:"-"` // JSON 응답에는 포함하지 않음
}

// ⭐️ [신규] API 요청/응답용 구조체
type AuthRequest struct {
	Email    string `json:"email"`
	Password string `json:"password"`
}
type AuthResponse struct {
	Token string `json:"token"`
}

// ⭐️ [신규] '좋아요' 요청 본문
type LikeRequest struct {
	FeedID string `json:"feed_id"`
}

var jwtKey = []byte("my_secret_key_12345")

type Claims struct {
	Email string `json:"email"`
	jwt.RegisteredClaims
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [신규] 2. DB 초기화 (테이블 생성 및 시드 데이터 삽입)

func setupDatabase(db *sql.DB) {
	// --- Feeds 테이블 ---
	db.Exec(`CREATE TABLE IF NOT EXISTS feeds (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        radius INTEGER,
        user_name TEXT,
        user_profile_image_url TEXT,
        post_image_url TEXT,
        content TEXT,
        likes_count INTEGER
    )`)

	// --- Deliveries 테이블 ---
	db.Exec(`CREATE TABLE IF NOT EXISTS deliveries (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        store_name TEXT,
        store_image_url TEXT,
        estimated_time_in_minutes INTEGER,
        status TEXT
    )`)

	// --- Shorts 테이블 ---
	db.Exec(`CREATE TABLE IF NOT EXISTS shorts (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        store_name TEXT,
        store_id TEXT,
        video_url TEXT
    )`)

	// ⭐️ [신규] Users 테이블 (비밀번호 해시 저장)
	db.Exec(`CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        email TEXT UNIQUE,
        hashed_password TEXT
    )`)

	// ⭐️ [신규] 5. '좋아요' 테이블
	// '누가'(user_id) '무엇을'(feed_id) 좋아했는지 기록
	// (UNIQUE 제약조건: 한 사람이 한 게시물에 '좋아요'는 한 번만 가능)
	db.Exec(`CREATE TABLE IF NOT EXISTS feed_likes (
        user_id INTEGER,
        feed_id INTEGER,
        PRIMARY KEY (user_id, feed_id),
        FOREIGN KEY (user_id) REFERENCES users (id),
        FOREIGN KEY (feed_id) REFERENCES feeds (id)
    )`)

	// --- 시드 데이터 삽입 (최초 1회만 실행되도록 INSERT OR IGNORE) ---
	log.Println("데이터베이스 시드 데이터 삽입 시도...")
	// (반경별 2개씩만 샘플로 삽입)
	db.Exec(`INSERT OR IGNORE INTO feeds (id, radius, user_name, content) VALUES
        (1, 5, 'SQLite-5km_user_1', '이것은 SQLite DB에서 온 5km 반경 피드입니다.'),
        (2, 5, 'SQLite-5km_user_2', '5km 반경의 두 번째 피드.'),
        (3, 10, 'SQLite-10km_user_1', '이것은 SQLite DB에서 온 10km 반경 피드입니다.'),
        (4, 10, 'SQLite-10km_user_2', '10km 반경의 두 번째 피드.'),
        (5, 15, 'SQLite-15km_user_1', '이것은 SQLite DB에서 온 15km 반경 피드입니다.'),
        (6, 15, 'SQLite-15km_user_2', '15km 반경의 두 번째 피드.')
    `)
	db.Exec(`INSERT OR IGNORE INTO deliveries (id, store_name, status) VALUES
        (1, 'SQLite-가게_A', '배달중'),
        (2, 'SQLite-가게_B', '조리중')
    `)
	db.Exec(`INSERT OR IGNORE INTO shorts (id, store_name, store_id) VALUES
        (1, 'SQLite-쇼츠가게_1', 'store_1'),
        (2, 'SQLite-쇼츠가게_2', 'store_2')
    `)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// ⭐️ [신규] 3. JWT 검증 '헬퍼' 함수
// (이 함수는 '미들웨어'로도 만들 수 있음)
func verifyJWT(r *http.Request) (*Claims, error) {
	authHeader := r.Header.Get("Authorization")
	if authHeader == "" {
		return nil, fmt.Errorf("Authorization 헤더가 없습니다")
	}

	tokenString := strings.TrimPrefix(authHeader, "Bearer ")
	if tokenString == authHeader {
		return nil, fmt.Errorf("Bearer 토큰 형식이 아닙니다")
	}

	claims := &Claims{}
	token, err := jwt.ParseWithClaims(tokenString, claims, func(token *jwt.Token) (interface{}, error) {
		return jwtKey, nil // ⭐️ 31단계의 '비밀 키'로 검증
	})

	if err != nil {
		if err == jwt.ErrSignatureInvalid {
			return nil, fmt.Errorf("서명이 유효하지 않습니다")
		}
		return nil, fmt.Errorf("토큰 파싱 오류: %v", err)
	}
	if !token.Valid {
		return nil, fmt.Errorf("토큰이 유효하지 않습니다")
	}

	// ⭐️ 검증 성공: 토큰에 담긴 'Claims' (사용자 이메일) 반환
	return claims, nil
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
func feedHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	var userID int // ⭐️ [신규] '0'이면 비로그인 사용자

	// 1. ⭐️ (수정) JWT 검증 및 'userID' 추출
	claims, err := verifyJWT(r)
	if err != nil {
		log.Printf("JWT 검증 실패 (비로그인/만료): %v", err)
		// (비로그인이어도 피드는 보여줘야 하므로, userID = 0으로 진행)
	} else {
		log.Printf("JWT 검증 성공. 사용자: %s", claims.Email)
		// ⭐️ (신규) '누가' 요청했는지 DB에서 user_id를 찾음
		err = db.QueryRow("SELECT id FROM users WHERE email = ?", claims.Email).Scan(&userID)
		if err != nil {
			log.Printf("DB 오류 (User ID 조회): %v", err)
			// (user_id를 못 찾아도 피드는 보여줘야 하므로, userID = 0으로 진행)
		}
	}

	log.Println("안드로이드로부터 /feed 요청 수신 (DB 조회)")
	radiusQuery := r.URL.Query().Get("radius")
	radius, _ := strconv.Atoi(radiusQuery)
	if radius == 0 {
		radius = 5
	}
	log.Printf("DB 조회: 'feeds' 테이블 (radius = %dkm)", radius)

	// ⭐️ [수정] 2. SQL 쿼리 (LEFT JOIN 추가)
	query := `
        SELECT 
            f.id, f.user_name, f.user_profile_image_url, f.post_image_url, f.content, f.likes_count,
            (l.user_id IS NOT NULL) AS isLiked
        FROM 
            feeds f
        LEFT JOIN 
            feed_likes l ON f.id = l.feed_id AND l.user_id = ?
        WHERE 
            f.radius = ?
    `
	rows, err := db.Query(query, userID, radius) // ⭐️ 'userID'를 '?'에 바인딩

	if err != nil {
		log.Printf("DB 쿼리 오류 (Feed JOIN): %v", err)
		http.Error(w, "DB 오류", http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var feedItems []FeedItem
	for rows.Next() {
		var item FeedItem
		// ⭐️ [수정] 3. Scan (isLiked 추가)
		err := rows.Scan(
			&item.ID, &item.UserName, &item.UserProfileImageUrl,
			&item.PostImageUrl, &item.Content, &item.LikesCount,
			&item.IsLiked, // ⭐️ JOIN 결과(true/false) 스캔
		)
		if err != nil {
			log.Printf("DB 스캔 오류 (Feed JOIN): %v", err)
			http.Error(w, "DB 오류", http.StatusInternalServerError)
			return
		}
		feedItems = append(feedItems, item)
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(feedItems)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
func deliveryHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	log.Println("안드로이드로부터 /delivery 요청 수신 (DB 조회)")
	rows, err := db.Query("SELECT id, store_name, store_image_url, estimated_time_in_minutes, status FROM deliveries")
	if err != nil {
		log.Printf("DB 쿼리 오류 (Delivery): %v", err)
		http.Error(w, "DB 오류", http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var deliveryItems []DeliveryItem
	for rows.Next() {
		var item DeliveryItem
		err := rows.Scan(&item.ID, &item.StoreName, &item.StoreImageUrl, &item.EstimatedTimeInMinutes, &item.Status)
		if err != nil {
			log.Printf("DB 스캔 오류 (Delivery): %v", err)
			http.Error(w, "DB 오류", http.StatusInternalServerError)
			return
		}
		deliveryItems = append(deliveryItems, item)
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(deliveryItems)
}

func shortsHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	log.Println("안드로이드로부터 /shorts 요청 수신 (DB 조회)")
	rows, err := db.Query("SELECT id, store_name, store_id, video_url FROM shorts")
	if err != nil {
		log.Printf("DB 쿼리 오류 (Shorts): %v", err)
		http.Error(w, "DB 오류", http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var shortsItems []ShortsItem
	for rows.Next() {
		var item ShortsItem
		err := rows.Scan(&item.ID, &item.StoreName, &item.StoreID, &item.VideoURL)
		if err != nil {
			log.Printf("DB 스캔 오류 (Shorts): %v", err)
			http.Error(w, "DB 오류", http.StatusInternalServerError)
			return
		}
		shortsItems = append(shortsItems, item)
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(shortsItems)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [신규] 4. /register 핸들러 (POST)
func registerHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	log.Println("안드로이드로부터 /register (회원가입) 요청 수신")
	var req AuthRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	// 비밀번호 해싱 (bcrypt)
	hashedPass, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		http.Error(w, "Failed to hash password", http.StatusInternalServerError)
		return
	}

	// DB에 사용자 삽입
	_, err = db.Exec("INSERT INTO users (email, hashed_password) VALUES (?, ?)", req.Email, string(hashedPass))
	if err != nil {
		log.Printf("DB 오류 (User Insert): %v", err)
		http.Error(w, "Email already exists", http.StatusConflict) // 409 Conflict
		return
	}

	w.WriteHeader(http.StatusCreated) // 201 Created
	fmt.Fprintln(w, "User registered successfully")
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [신규] 5. /login 핸들러 (POST)
func loginHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	log.Println("안드로이드로부터 /login (로그인) 요청 수신")
	var req AuthRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	// DB에서 사용자 조회
	var user User
	err := db.QueryRow("SELECT id, email, hashed_password FROM users WHERE email = ?", req.Email).Scan(&user.ID, &user.Email, &user.HashedPassword)
	if err != nil {
		http.Error(w, "Invalid email or password", http.StatusUnauthorized) // 401
		return
	}

	// 해시된 비밀번호 비교
	err = bcrypt.CompareHashAndPassword([]byte(user.HashedPassword), []byte(req.Password))
	if err != nil {
		http.Error(w, "Invalid email or password", http.StatusUnauthorized) // 401
		return
	}

	// --- 로그인 성공: JWT 발급 ---
	expirationTime := time.Now().Add(24 * time.Hour) // 24시간 유효
	claims := &Claims{
		Email: user.Email,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(expirationTime),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	tokenString, err := token.SignedString(jwtKey)
	if err != nil {
		http.Error(w, "Failed to create token", http.StatusInternalServerError)
		return
	}

	log.Printf("로그인 성공 (User: %s), JWT 발급", user.Email)

	// ⭐️ 토큰을 JSON으로 응답
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(AuthResponse{Token: tokenString})
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [수정] 5. '좋아요' 핸들러 (카운트 +1 추가)
func likeFeedHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	log.Println("안드로이드로부터 /like (좋아요) 요청 수신")
	claims, err := verifyJWT(r) // --- 1. (인가) ---
	if err != nil {
		http.Error(w, "Authentication required", http.StatusUnauthorized)
		return
	}
	var req LikeRequest // --- 2. (요청 파싱) ---
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}
	var userID int // --- 3. (DB 작업) '누가' ---
	err = db.QueryRow("SELECT id FROM users WHERE email = ?", claims.Email).Scan(&userID)
	if err != nil {
		http.Error(w, "User not found", http.StatusNotFound)
		return
	}

	// --- 4. (DB 작업) '좋아요' 삽입 (INSERT OR IGNORE) ---
	res, err := db.Exec("INSERT OR IGNORE INTO feed_likes (user_id, feed_id) VALUES (?, ?)", userID, req.FeedID)
	if err != nil {
		http.Error(w, "Database error (feed_likes)", http.StatusInternalServerError)
		return
	}

	// ⭐️ [수정] 5. (DB 작업) '좋아요 카운트' +1
	rowsAffected, _ := res.RowsAffected()
	if rowsAffected > 0 { // ⭐️ 'INSERT'에 성공했을 때만 (중복 아닐 때)
		_, err = db.Exec("UPDATE feeds SET likes_count = COALESCE(likes_count, 0) + 1 WHERE id = ?", req.FeedID)
		if err != nil {
			http.Error(w, "Database error (feeds count)", http.StatusInternalServerError)
			return
		}
	}
	log.Printf("좋아요 성공: UserID %d -> FeedID %s", userID, req.FeedID)
	w.WriteHeader(http.StatusOK)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [신규] 6. '좋아요 취소' 핸들러 (POST)
func unlikeFeedHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	log.Println("안드로이드로부터 /unlike (좋아요 취소) 요청 수신")
	claims, err := verifyJWT(r) // --- 1. (인가) ---
	if err != nil {
		http.Error(w, "Authentication required", http.StatusUnauthorized)
		return
	}
	var req LikeRequest // --- 2. (요청 파싱) ---
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}
	var userID int // --- 3. (DB 작업) '누가' ---
	err = db.QueryRow("SELECT id FROM users WHERE email = ?", claims.Email).Scan(&userID)
	if err != nil {
		http.Error(w, "User not found", http.StatusNotFound)
		return
	}

	// --- 4. (DB 작업) '좋아요' 삭제 (DELETE) ---
	res, err := db.Exec("DELETE FROM feed_likes WHERE user_id = ? AND feed_id = ?", userID, req.FeedID)
	if err != nil {
		http.Error(w, "Database error (feed_likes delete)", http.StatusInternalServerError)
		return
	}

	// ⭐️ [신규] 5. (DB 작업) '좋아요 카운트' -1
	rowsAffected, _ := res.RowsAffected()
	if rowsAffected > 0 { // ⭐️ 'DELETE'에 성공했을 때만
		_, err = db.Exec("UPDATE feeds SET likes_count = COALESCE(likes_count, 1) - 1 WHERE id = ?", req.FeedID)
		if err != nil {
			http.Error(w, "Database error (feeds count)", http.StatusInternalServerError)
			return
		}
	}
	log.Printf("좋아요 취소 성공: UserID %d -> FeedID %s", userID, req.FeedID)
	w.WriteHeader(http.StatusOK)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

func main() {
	db, err := sql.Open("sqlite", "./babful.db") // ⭐️ (22단계 Plan B)
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()
	setupDatabase(db)

	// ⭐️ [수정] 6. 핸들러 라우팅 수정 (POST만 허용하도록)
	http.HandleFunc("/feed", func(w http.ResponseWriter, r *http.Request) {
		feedHandler(w, r, db)
	})
	http.HandleFunc("/delivery", func(w http.ResponseWriter, r *http.Request) {
		deliveryHandler(w, r, db)
	})
	http.HandleFunc("/shorts", func(w http.ResponseWriter, r *http.Request) {
		shortsHandler(w, r, db)
	})

	// ⭐️ [신규] 7. Auth 핸들러 (POST 전용)
	http.HandleFunc("/register", func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodPost {
			http.Error(w, "Only POST method is allowed", http.StatusMethodNotAllowed)
			return
		}
		registerHandler(w, r, db)
	})
	http.HandleFunc("/login", func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodPost {
			http.Error(w, "Only POST method is allowed", http.StatusMethodNotAllowed)
			return
		}
		loginHandler(w, r, db)
	})

	// ⭐️ [신규] 8. Like 핸들러 (POST 전용)
	http.HandleFunc("/like", func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodPost {
			http.Error(w, "Only POST method is allowed", http.StatusMethodNotAllowed)
			return
		}
		likeFeedHandler(w, r, db)
	})

	// ⭐️ [신규] 9. Unlike 핸들러 (POST 전용)
	http.HandleFunc("/unlike", func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodPost {
			http.Error(w, "Only POST method is allowed", http.StatusMethodNotAllowed)
			return
		}
		unlikeFeedHandler(w, r, db)
	})

	fmt.Println("Go 백엔드 서버(Auth v6 - Like Toggle)가 http://localhost:8080 에서 실행 중입니다...")
	log.Fatal(http.ListenAndServe(":8080", nil)) // ⭐️ 8080 포트
}
