package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"io" // ⭐️ [신규] HTTP 응답 읽기용
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
// --- 1. 구조체 정의 ---
type FeedItem struct {
	ID                  string  `json:"id"`
	UserName            string  `json:"user_name"`
	UserProfileImageUrl *string `json:"user_profile_image_url"`
	PostImageUrl        *string `json:"post_image_url"`
	Content             string  `json:"content"`
	LikesCount          *int    `json:"likes_count"`
	IsLiked             bool    `json:"is_liked"`
}
type DeliveryItem struct {
	ID                     string  `json:"id"`
	StoreName              string  `json:"store_name"`
	StoreImageUrl          *string `json:"store_image_url"`
	EstimatedTimeInMinutes *int    `json:"estimated_time_in_minutes"`
	Status                 string  `json:"status"`
	Lat                    float64 `json:"lat"` // 41단계 (지도 좌표)
	Lng                    float64 `json:"lng"` // 41단계 (지도 좌표)
}
type ShortsItem struct {
	ID        string  `json:"id"`
	StoreName string  `json:"store_name"`
	StoreID   string  `json:"store_id"`
	VideoURL  *string `json:"video_url"`
}
// ⭐️ [수정] User 구조체 (Role 추가)
type User struct {
    ID             int    `json:"id"`
    Email          string `json:"email"`
    HashedPassword string `json:"-"`
    Points         int    `json:"points"`
    Role           string `json:"role"` // ⭐️ "customer" or "owner"
}
// ⭐️ [수정] AuthRequest (Role 추가 - 회원가입용)
type AuthRequest struct {
    Email    string `json:"email"`
    Password string `json:"password"`
    Role     string `json:"role"` // ⭐️ 회원가입 시 선택 (기본값: customer)
}
// ⭐️ [수정] AuthResponse (Role 추가 - 로그인 시 클라이언트가 알 수 있게)
type AuthResponse struct {
    Token string `json:"token"`
    Role  string `json:"role"` // ⭐️
}
type LikeRequest struct {
	FeedID string `json:"feed_id"`
}
type SubscribeRequest struct {
	StoreID string `json:"store_id"`
}
type StoreInfo struct {
	ID           string  `json:"id"`
	StoreName    string  `json:"store_name"`
	BannerImage  *string `json:"banner_image"`
	IsSubscribed bool    `json:"is_subscribed"`
}
type Transaction struct {
	ID        int       `json:"id"`
	UserID    int       `json:"user_id"`
	Amount    int       `json:"amount"`
	Type      string    `json:"type"`
	Timestamp time.Time `json:"timestamp"`
	OrderID   *string   `json:"order_id"` // 40.1단계 (악용 방지)
}
type PaymentRequest struct {
	AmountPaid int    `json:"amount_paid"`
	OrderID    string `json:"order_id"`
}

var jwtKey = []byte("my_secret_key_12345")

type Claims struct {
	Email string `json:"email"`
	jwt.RegisteredClaims
}

// ⭐️ [신규] Google Directions API 응답 구조체 (필요한 것만 정의)
type GoogleDirectionsResponse struct {
	Routes []struct {
		OverviewPolyline struct {
			Points string `json:"points"` // ⭐️ 이 '암호화된 문자열'이 경로 선 데이터입니다.
		} `json:"overview_polyline"`
	} `json:"routes"`
	Status string `json:"status"`
}

// ⭐️ [신규] Store 구조체 (DB 저장용)
// 기존 StoreInfo(조회용)와 달리, OwnerID와 Lat/Lng 등 관리 정보 포함
type Store struct {
    ID          int     `json:"id"`
    OwnerID     int     `json:"owner_id"`
    Name        string  `json:"name"`
    Description string  `json:"description"`
    Lat         float64 `json:"lat"`
    Lng         float64 `json:"lng"`
    ImageUrl    string  `json:"image_url"`
}

// ⭐️ [신규] 가게 등록 요청
type CreateStoreRequest struct {
    Name        string  `json:"name"`
    Description string  `json:"description"`
    Lat         float64 `json:"lat"`
    Lng         float64 `json:"lng"`
}

// ⭐️ [신규] Menu 구조체
type Menu struct {
    ID       int    `json:"id"`
    StoreID  int    `json:"store_id"`
    Name     string `json:"name"`
    Price    int    `json:"price"`
    ImageUrl string `json:"image_url"`
}

// ⭐️ [신규] 메뉴 생성 요청
type CreateMenuRequest struct {
    StoreID int    `json:"store_id"`
    Name    string `json:"name"`
    Price   int    `json:"price"`
}

// ⭐️ [필수] 42단계에서 발급받은 본인의 API Key를 여기에 넣으세요.
const GOOGLE_MAPS_API_KEY = "Your_Api_key"

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

	// ⭐️ [수정] Deliveries 테이블 (lat, lng 추가)
	db.Exec(`CREATE TABLE IF NOT EXISTS deliveries (
		id INTEGER PRIMARY KEY AUTOINCREMENT,
		store_name TEXT,
		store_image_url TEXT,
		estimated_time_in_minutes INTEGER,
		status TEXT,
		lat REAL, 
		lng REAL
	)`)

	// --- Shorts 테이블 ---
	db.Exec(`CREATE TABLE IF NOT EXISTS shorts (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        store_name TEXT,
        store_id TEXT,
        video_url TEXT
    )`)

	// ⭐️ [수정] Users 테이블 (role 컬럼 추가)
    db.Exec(`CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        email TEXT UNIQUE,
        hashed_password TEXT,
        points INTEGER DEFAULT 0,
        role TEXT DEFAULT 'customer' 
    )`)

	// ⭐️ [수정] 7. '포인트 내역' 테이블 (악용 방지)
	db.Exec(`CREATE TABLE IF NOT EXISTS transactions (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        user_id INTEGER,
        amount INTEGER,
        type TEXT,
        timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
        order_id TEXT UNIQUE, 
        FOREIGN KEY (user_id) REFERENCES users (id)
    )`) // ⭐️ order_id UNIQUE 제약조건 추가

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

	// ⭐️ [신규] 6. '구독' 테이블
	// '누가'(user_id) '어느 가게를'(store_id) 구독했는지
	db.Exec(`CREATE TABLE IF NOT EXISTS subscriptions (
        user_id INTEGER,
        store_id TEXT, 
        PRIMARY KEY (user_id, store_id),
        FOREIGN KEY (user_id) REFERENCES users (id)
    )`)

	// ⭐️ [신규] Stores 테이블 (점주가 관리하는 가게)
    // deliveries 테이블은 '배달 상태' 중심이고, stores는 '가게 정보' 중심 (나중에 통합 가능)
    db.Exec(`CREATE TABLE IF NOT EXISTS stores (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        owner_id INTEGER,
        name TEXT,
        description TEXT,
        lat REAL,
        lng REAL,
        image_url TEXT,
        FOREIGN KEY (owner_id) REFERENCES users (id)
    )`)

	// ⭐️ [신규] Menus 테이블
    db.Exec(`CREATE TABLE IF NOT EXISTS menus (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        store_id INTEGER,
        name TEXT,
        price INTEGER,
        image_url TEXT,
        FOREIGN KEY (store_id) REFERENCES stores (id)
    )`)

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
	// ⭐️ [수정] Deliveries 시드 데이터 (좌표 포함 - 서울 강남역 인근 예시)
	db.Exec(`INSERT OR IGNORE INTO deliveries (id, store_name, status, lat, lng) VALUES
		(1, 'SQLite-가게_A (강남역)', '배달중', 37.4979, 127.0276),
		(2, 'SQLite-가게_B (역삼역)', '조리중', 37.5006, 127.0364),
        (3, 'SQLite-가게_C (신논현)', '준비중', 37.5045, 127.0250)
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
// ⭐️ [수정] deliveryHandler (좌표 스캔 추가)
func deliveryHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	log.Println("안드로이드로부터 /delivery 요청 수신 (DB 조회)")

	// ⭐️ lat, lng 컬럼 조회 추가
	rows, err := db.Query("SELECT id, store_name, store_image_url, estimated_time_in_minutes, status, lat, lng FROM deliveries")
	if err != nil {
		log.Printf("DB 쿼리 오류 (Delivery): %v", err)
		http.Error(w, "DB 오류", http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var deliveryItems []DeliveryItem
	for rows.Next() {
		var item DeliveryItem
		// ⭐️ lat, lng 스캔 추가
		err := rows.Scan(&item.ID, &item.StoreName, &item.StoreImageUrl, &item.EstimatedTimeInMinutes, &item.Status, &item.Lat, &item.Lng)
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

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
// ⭐️ [수정] 회원가입 핸들러
func registerHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
    var req AuthRequest
    if json.NewDecoder(r.Body).Decode(&req) != nil { http.Error(w, "Bad Request", 400); return }
    
    hashedPass, _ := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
    
    // Role 기본값 처리
    if req.Role == "" { req.Role = "customer" }

    _, err := db.Exec("INSERT INTO users (email, hashed_password, role) VALUES (?, ?, ?)", req.Email, string(hashedPass), req.Role)
    if err != nil { http.Error(w, "Email exists", 409); return }
    
    w.WriteHeader(http.StatusCreated)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [수정] 로그인 핸들러
func loginHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
    var req AuthRequest
    if json.NewDecoder(r.Body).Decode(&req) != nil { http.Error(w, "Bad Request", 400); return }

    var user User
    err := db.QueryRow("SELECT id, email, hashed_password, role FROM users WHERE email = ?", req.Email).Scan(&user.ID, &user.Email, &user.HashedPassword, &user.Role)
    if err != nil || bcrypt.CompareHashAndPassword([]byte(user.HashedPassword), []byte(req.Password)) != nil {
        http.Error(w, "Unauthorized", 401); return
    }

    // JWT 발급 (Role 포함)
    expirationTime := time.Now().Add(24 * time.Hour)
    claims := &Claims{ Email: user.Email, Role: user.Role, RegisteredClaims: jwt.RegisteredClaims{ ExpiresAt: jwt.NewNumericDate(expirationTime) } }
    token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
    tokenString, _ := token.SignedString(jwtKey)

    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(AuthResponse{Token: tokenString, Role: user.Role}) // Role 반환
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
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [수정] 6. storeHandler (배달/쇼츠 ID 호환)
// ⭐️ [수정] storeHandler (43단계 Fix: 배달/쇼츠 ID 호환)
func storeHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	storeID := strings.TrimPrefix(r.URL.Path, "/store/")
	log.Printf("안드로이드로부터 /store/%s (가게 정보) 요청 수신", storeID)

	var userID int
	if claims, err := verifyJWT(r); err == nil {
		db.QueryRow("SELECT id FROM users WHERE email = ?", claims.Email).Scan(&userID)
	}

	var store StoreInfo
	// 1. Deliveries 테이블 먼저 조회 (ID가 숫자인 경우)
	queryDelivery := `SELECT d.id, d.store_name, (sub.user_id IS NOT NULL) FROM deliveries d LEFT JOIN subscriptions sub ON d.id = sub.store_id AND sub.user_id = ? WHERE d.id = ?`
	err := db.QueryRow(queryDelivery, userID, storeID).Scan(&store.ID, &store.StoreName, &store.IsSubscribed)

	if err == sql.ErrNoRows {
		// 2. 없으면 Shorts 테이블 조회 (ID가 문자열인 경우)
		log.Printf("Deliveries 테이블에 없음. Shorts 테이블 조회 시도...")
		queryShorts := `SELECT s.store_id, s.store_name, (sub.user_id IS NOT NULL) FROM shorts s LEFT JOIN subscriptions sub ON s.store_id = sub.store_id AND sub.user_id = ? WHERE s.store_id = ?`
		err = db.QueryRow(queryShorts, userID, storeID).Scan(&store.ID, &store.StoreName, &store.IsSubscribed)
	}

	if err != nil {
		log.Printf("DB 쿼리 오류 (Store 찾기 실패): %v", err)
		http.Error(w, "Store not found", http.StatusNotFound)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(store)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [신규] 8. '구독' 핸들러 (POST)
func subscribeHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	log.Println("안드로이드로부터 /subscribe (구독) 요청 수신")
	claims, err := verifyJWT(r) // --- 1. (인가) ---
	if err != nil {
		http.Error(w, "Authentication required", http.StatusUnauthorized)
		return
	}
	var req SubscribeRequest // --- 2. (요청 파싱) ---
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

	// --- 4. (DB 작업) '구독' 삽입 (INSERT OR IGNORE) ---
	_, err = db.Exec("INSERT OR IGNORE INTO subscriptions (user_id, store_id) VALUES (?, ?)", userID, req.StoreID)
	if err != nil {
		http.Error(w, "Database error (subscriptions)", http.StatusInternalServerError)
		return
	}

	log.Printf("구독 성공: UserID %d -> StoreID %s", userID, req.StoreID)
	w.WriteHeader(http.StatusOK)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [신규] 9. '구독 취소' 핸들러 (POST)
func unsubscribeHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	log.Println("안드로이드로부터 /unsubscribe (구독 취소) 요청 수신")
	claims, err := verifyJWT(r) // --- 1. (인가) ---
	if err != nil {
		http.Error(w, "Authentication required", http.StatusUnauthorized)
		return
	}
	var req SubscribeRequest // --- 2. (요청 파싱) ---
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

	// --- 4. (DB 작업) '구독' 삭제 (DELETE) ---
	_, err = db.Exec("DELETE FROM subscriptions WHERE user_id = ? AND store_id = ?", userID, req.StoreID)
	if err != nil {
		http.Error(w, "Database error (subscriptions delete)", http.StatusInternalServerError)
		return
	}

	log.Printf("구독 취소 성공: UserID %d -> StoreID %s", userID, req.StoreID)
	w.WriteHeader(http.StatusOK)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [신규] 12. '포인트 내역' 핸들러 (GET)
func pointHistoryHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	log.Println("안드로이드로부터 /points/history 요청 수신")
	claims, err := verifyJWT(r) // --- 1. (인가) ---
	if err != nil {
		http.Error(w, "Authentication required", http.StatusUnauthorized)
		return
	}
	var userID int // --- 2. (DB 작업) '누가' ---
	err = db.QueryRow("SELECT id FROM users WHERE email = ?", claims.Email).Scan(&userID)
	if err != nil {
		http.Error(w, "User not found", http.StatusNotFound)
		return
	}

	// --- 3. (DB 작업) '내역' 조회 ---
	rows, err := db.Query("SELECT id, user_id, amount, type, timestamp FROM transactions WHERE user_id = ? ORDER BY timestamp DESC", userID)
	if err != nil {
		http.Error(w, "Database error", http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	// ⭐️ [수정] 'nil' 슬라이스 대신 '빈(empty)' 슬라이스로 초기화
	transactions := make([]Transaction, 0) // var transactions []Transaction -> make(...)

	for rows.Next() {
		var t Transaction
		if err := rows.Scan(&t.ID, &t.UserID, &t.Amount, &t.Type, &t.Timestamp); err != nil {
			http.Error(w, "Scan error", http.StatusInternalServerError)
			return
		}
		transactions = append(transactions, t)
	}

	// ⭐️ (이제 'transactions'가 비어있으면 'null'이 아닌 '[]'를 반환함)
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(transactions)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [복구] paymentSuccessHandler (40.1단계 코드)
func paymentSuccessHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
	log.Println("안드로이드로부터 /payment/success 요청 수신")
	claims, err := verifyJWT(r)
	if err != nil {
		http.Error(w, "Auth required", 401)
		return
	}
	var req PaymentRequest
	if json.NewDecoder(r.Body).Decode(&req) != nil {
		http.Error(w, "Bad Request", 400)
		return
	}

	var userID int
	db.QueryRow("SELECT id FROM users WHERE email = ?", claims.Email).Scan(&userID)

	pointsEarned := int(float64(req.AmountPaid) * 0.01)
	tx, _ := db.Begin()
	_, err = tx.Exec("INSERT INTO transactions (user_id, amount, type, order_id) VALUES (?, ?, ?, ?)", userID, pointsEarned, "적립: 주문", req.OrderID)
	if err != nil {
		tx.Rollback()
		http.Error(w, "Duplicate/Error", 409)
		return
	}
	tx.Exec("UPDATE users SET points = points + ? WHERE id = ?", pointsEarned, userID)
	tx.Commit()

	log.Printf("포인트 적립 성공: UserID %d, %dP 적립", userID, pointsEarned)
	w.WriteHeader(http.StatusOK)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [수정] 내 프로필 핸들러
func profileHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
    claims, err := verifyJWT(r); if err != nil { http.Error(w, "Auth required", 401); return }
    
    var user User
    // role 컬럼 추가 조회
    err = db.QueryRow("SELECT id, email, points, role FROM users WHERE email = ?", claims.Email).Scan(&user.ID, &user.Email, &user.Points, &user.Role)
    if err != nil { http.Error(w, "User not found", 404); return }
    
    w.Header().Set("Content-Type", "application/json"); json.NewEncoder(w).Encode(user)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [신규] 14. 길찾기 핸들러 (Proxy)
func directionsHandler(w http.ResponseWriter, r *http.Request) {
	// 1. 파라미터 파싱 (origin=위도,경도 & dest=위도,경도)
	origin := r.URL.Query().Get("origin")
	dest := r.URL.Query().Get("dest")

	log.Printf("길찾기 요청: %s -> %s", origin, dest)

	if origin == "" || dest == "" {
		http.Error(w, "Missing origin or dest", http.StatusBadRequest)
		return
	}

	// 2. Google Directions API 호출 (Go 서버가 대신 호출)
	googleURL := fmt.Sprintf(
		"https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s",
		origin, dest, GOOGLE_MAPS_API_KEY,
	)

	resp, err := http.Get(googleURL)
	if err != nil {
		log.Printf("Google API 호출 실패: %v", err)
		http.Error(w, "Failed to call Google API", http.StatusInternalServerError)
		return
	}
	defer resp.Body.Close()

	// 3. Google 응답 읽기
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		http.Error(w, "Failed to read response", http.StatusInternalServerError)
		return
	}

	// 4. 안드로이드에게 그대로 전달 (또는 파싱해서 points만 줄 수도 있음)
	// 여기서는 Google의 JSON 전체를 그대로 토스(Pass-through)합니다.
	w.Header().Set("Content-Type", "application/json")
	w.Write(body)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [신규] 15. 점주용: 내 가게 등록 (POST)
func createMyStoreHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
    log.Println("점주로부터 /owner/store (가게 등록) 요청 수신")
    claims, err := verifyJWT(r)
    if err != nil { http.Error(w, "Auth required", 401); return }
    
    // 1. 권한 체크 (Role이 owner인지)
    if claims.Role != "owner" {
        http.Error(w, "Forbidden: Only owners can create stores", 403)
        return
    }

    var req CreateStoreRequest
    if json.NewDecoder(r.Body).Decode(&req) != nil { http.Error(w, "Bad Request", 400); return }

    var userID int
    db.QueryRow("SELECT id FROM users WHERE email = ?", claims.Email).Scan(&userID)

    // 2. 가게 생성 (이미 있는지 체크 로직은 생략 - 1인 다점포 허용 가정)
    // (이미지 URL은 임시로 고정)
    res, err := db.Exec("INSERT INTO stores (owner_id, name, description, lat, lng, image_url) VALUES (?, ?, ?, ?, ?, ?)",
        userID, req.Name, req.Description, req.Lat, req.Lng, "https://picsum.photos/200")
    
    if err != nil {
        log.Printf("가게 생성 실패: %v", err)
        http.Error(w, "DB Error", 500)
        return
    }
    
    id, _ := res.LastInsertId()
    log.Printf("가게 등록 성공: StoreID %d (OwnerID %d)", id, userID)
    w.WriteHeader(http.StatusCreated)
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*																																						 */
/*																																						 */
/*																																						 */
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ⭐️ [신규] 16. 점주용: 내 가게 조회 (GET)
func getMyStoreHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
    claims, err := verifyJWT(r)
    if err != nil { http.Error(w, "Auth required", 401); return }

    var userID int
    db.QueryRow("SELECT id FROM users WHERE email = ?", claims.Email).Scan(&userID)

    // 가장 최근에 등록한 가게 1개만 가져오기 (단순화)
    var store Store
    err = db.QueryRow("SELECT id, owner_id, name, description, lat, lng, image_url FROM stores WHERE owner_id = ? ORDER BY id DESC LIMIT 1", userID).Scan(
        &store.ID, &store.OwnerID, &store.Name, &store.Description, &store.Lat, &store.Lng, &store.ImageUrl)

    if err == sql.ErrNoRows {
        http.Error(w, "No store found", 404)
        return
    }

    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(store)
}

// ⭐️ [신규] 17. 메뉴 등록 핸들러 (점주용 POST)
func createMenuHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
    claims, err := verifyJWT(r); if err != nil { http.Error(w, "Auth required", 401); return }
    if claims.Role != "owner" { http.Error(w, "Forbidden", 403); return }

    var req CreateMenuRequest
    if json.NewDecoder(r.Body).Decode(&req) != nil { http.Error(w, "Bad Request", 400); return }

    // (본인 가게인지 확인하는 로직은 생략 - 간단하게 구현)
    _, err = db.Exec("INSERT INTO menus (store_id, name, price, image_url) VALUES (?, ?, ?, ?)",
        req.StoreID, req.Name, req.Price, "https://picsum.photos/150") // 이미지 더미
    
    if err != nil { http.Error(w, "DB Error", 500); return }
    w.WriteHeader(http.StatusCreated)
}

// ⭐️ [신규] 18. 가게 메뉴 조회 핸들러 (공용 GET)
func getStoreMenusHandler(w http.ResponseWriter, r *http.Request, db *sql.DB) {
    // URL: /store/{id}/menus
    // Path 파싱이 복잡하므로, Query Param 방식 사용: /store/menus?store_id=1
    storeID := r.URL.Query().Get("store_id")
    if storeID == "" { http.Error(w, "Missing store_id", 400); return }

    rows, err := db.Query("SELECT id, store_id, name, price, image_url FROM menus WHERE store_id = ?", storeID)
    if err != nil { http.Error(w, "DB Error", 500); return }
    defer rows.Close()

    var menus []Menu
    for rows.Next() {
        var m Menu
        rows.Scan(&m.ID, &m.StoreID, &m.Name, &m.Price, &m.ImageUrl)
        menus = append(menus, m)
    }
    // 빈 배열 처리
    if menus == nil { menus = []Menu{} }

    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(menus)
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

// --- 5. Main 함수 ---
func main() {
	db, err := sql.Open("sqlite", "./babful.db")
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()
	setupDatabase(db)

	// 라우팅 등록 (필수 확인)
	http.HandleFunc("/feed", func(w http.ResponseWriter, r *http.Request) { feedHandler(w, r, db) })
	http.HandleFunc("/delivery", func(w http.ResponseWriter, r *http.Request) { deliveryHandler(w, r, db) })
	http.HandleFunc("/shorts", func(w http.ResponseWriter, r *http.Request) { shortsHandler(w, r, db) })
	http.HandleFunc("/store/", func(w http.ResponseWriter, r *http.Request) {
		if r.Method != "GET" {
			http.Error(w, "GET only", 405)
			return
		}
		storeHandler(w, r, db) // ⭐️ 수정된 핸들러
	})

	// ⭐️ [복구] 결제 핸들러 라우팅
	http.HandleFunc("/payment/success", func(w http.ResponseWriter, r *http.Request) {
		if r.Method != "POST" {
			http.Error(w, "POST only", 405)
			return
		}
		paymentSuccessHandler(w, r, db)
	})

	// (기타 라우팅)
	http.HandleFunc("/register", func(w http.ResponseWriter, r *http.Request) { registerHandler(w, r, db) })
	http.HandleFunc("/login", func(w http.ResponseWriter, r *http.Request) { loginHandler(w, r, db) })
	http.HandleFunc("/like", func(w http.ResponseWriter, r *http.Request) { likeFeedHandler(w, r, db) })
	http.HandleFunc("/unlike", func(w http.ResponseWriter, r *http.Request) { unlikeFeedHandler(w, r, db) })
	http.HandleFunc("/subscribe", func(w http.ResponseWriter, r *http.Request) { subscribeHandler(w, r, db) })
	http.HandleFunc("/unsubscribe", func(w http.ResponseWriter, r *http.Request) { unsubscribeHandler(w, r, db) })
	http.HandleFunc("/profile/me", func(w http.ResponseWriter, r *http.Request) { profileHandler(w, r, db) })
	http.HandleFunc("/points/history", func(w http.ResponseWriter, r *http.Request) { pointHistoryHandler(w, r, db) })
	// ⭐️ [신규] 14. Directions 라우팅
	http.HandleFunc("/directions", func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodGet {
			http.Error(w, "GET only", http.StatusMethodNotAllowed)
			return
		}
		directionsHandler(w, r)
	})

	// ⭐️ [신규] 점주용 라우팅
    http.HandleFunc("/owner/store", func(w http.ResponseWriter, r *http.Request) {
        if r.Method == "POST" {
            createMyStoreHandler(w, r, db)
        } else if r.Method == "GET" {
            getMyStoreHandler(w, r, db)
        } else {
            http.Error(w, "Method not allowed", 405)
        }
    })
	// ⭐️ [신규] 메뉴 관련 라우팅
    http.HandleFunc("/owner/menu", func(w http.ResponseWriter, r *http.Request) {
        if r.Method == "POST" { createMenuHandler(w, r, db) }
    })
    
    // (고객/점주 공용 조회)
    http.HandleFunc("/store/menus", func(w http.ResponseWriter, r *http.Request) {
        if r.Method == "GET" { getStoreMenusHandler(w, r, db) }
    })
	
	fmt.Println("Go 백엔드 서버(Auth v14 - Owner Role)가 http://localhost:8080 에서 실행 중입니다...")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
