package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"time"
)

// --- [ 1. 데이터 모델 정의 (DTO) ] ---

type DeliveryRequestDto struct {
	Restaurant string `json:"restaurant"`
	Menu       string `json:"menu"`
	Address    string `json:"address"`
}

type ShortsItemDto struct {
	ID        int    `json:"id"`
	VideoUrl  string `json:"video_url"`
	Title     string `json:"title"`
	IsLiked   bool   `json:"is_liked"`
}

type PostDto struct {
	PostID         int    `json:"post_id"`
	UserName       string `json:"user_name"`
	PostContent    string `json:"post_content"`
	ImageUrl       string `json:"image_url"`
	IsLikedByUser  bool   `json:"is_liked_by_user"`
}

// UserApi (POST /auth/login) 요청용 DTO
type LoginRequestDto struct {
	Email    string `json:"email"`
	Password string `json:"password"`
}

// UserApi (POST /auth/login) 응답용 DTO
type UserDto struct {
	UserID       string `json:"user_id"`
	EmailAddress string `json:"email_address"`
	Nickname     string `json:"nickname"`
	AuthToken    string `json:"auth_token"`
}

// --- [ 2. API 핸들러 (로직) 정의 ] ---

// (FeedApi) GET /posts 핸들러
func deliveryHandler(w http.ResponseWriter, r *http.Request) {
	var req DeliveryRequestDto
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	log.Printf("Delivery request received: %v", req)
	
	// (임시) 주문 성공 응답
	resp := DeliveryResponseDto{
		OrderID:       fmt.Sprintf("order_%d", time.Now().Unix()),
		Status:        "PENDING",
		EstimatedTime: 30, // 30분
	}
	
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(resp)
}

func shortsHandler(w http.ResponseWriter, r *http.Request) {
	dummyShorts := []ShortsItemDto{
		{ID: 1, VideoUrl: "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", Title: "Big Buck Bunny (Go)", IsLiked: true},
		{ID: 2, VideoUrl: "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4", Title: "Elephants Dream (Go)", IsLiked: false},
	}
	log.Println("Responding to /shorts request...")
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(dummyShorts)
}

func shortsCommentHandler(w http.ResponseWriter, r *http.Request) {
	// (id 파싱 로직 생략) ...
	dummyComments := []ShortsCommentDto{
		{ID: "sc1", Author: "Go User", Content: "Go 서버 댓글입니다!"},
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(dummyComments)
}

func feedHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodGet {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	// (임시) 3단계 더미 데이터 생성
	dummyPosts := []PostDto{
		{PostID: 1, UserName: "Go Server", PostContent: "첫 번째 포스트 (from Go!)", ImageUrl: "", IsLikedByUser: true},
		{PostID: 2, UserName: "net/http", PostContent: "이것은 Go 서버의 응답입니다.", ImageUrl: "", IsLikedByUser: false},
	}

	log.Println("Responding to /posts request...")
	// JSON으로 인코딩하여 응답
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(dummyPosts)
}

// (UserApi) POST /auth/login 핸들러
func loginHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	// (1) 요청 JSON(Body) 파싱
	var req LoginRequestDto
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	// (임시) 3단계 로그인 로직 (ID/PW 검증 시뮬레이션)
	log.Printf("Login attempt: %s\n", req.Email)

	// (2) 로그인 성공 응답 (DTO) 생성
	responseDto := UserDto{
		UserID:       "uid-go-123",
		EmailAddress: req.Email,
		Nickname:     "Go 독학자",
		AuthToken:    fmt.Sprintf("go_token_%d", time.Now().Unix()),
	}

	// (3) JSON으로 인코딩하여 응답
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(responseDto)
}

// (UserApi) GET /users/me 핸들러 (프로필)
func profileHandler(w http.ResponseWriter, r *http.Request) {
	// (임시) 3단계 토큰 검증
	token := r.Header.Get("Authorization")
	log.Printf("Profile request with token: %s\n", token)
	
	if token == "" {
		http.Error(w, "Authorization header required", http.StatusUnauthorized)
		return
	}
	
	// (임시) 토큰이 유효하다고 가정하고 유저 정보 응답
	responseDto := UserDto{
		UserID:       "uid-go-123",
		EmailAddress: "test@user.com",
		Nickname:     "Go 독학자",
		AuthToken:    token,
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(responseDto)
}


// --- [ 3. 메인 함수 (서버 실행) ] ---

func main() {
	// (1) 라우터(Mux) 설정
	mux := http.NewServeMux()
	
	// (2) URL 엔드포인트와 핸들러 함수 연결 (Android Api 명세와 일치)
	mux.HandleFunc("/posts", feedHandler)
	mux.HandleFunc("/auth/login", loginHandler)
	mux.HandleFunc("/users/me", profileHandler)
	// TODO: /posts/{id}/like, /auth/logout 등 나머지 핸들러 구현
	
	// 🚨 [New] Delivery, Shorts 라우트
	mux.HandleFunc("/delivery/submit", deliveryHandler)
	mux.HandleFunc("/shorts", shortsHandler)
	mux.HandleFunc("/shorts/", shortsCommentHandler)
	// (3) 서버 시작
	log.Println("Go API server starting on :8080...")
	if err := http.ListenAndServe(":8080", mux); err != nil {
		log.Fatal(err)
	}
}