package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strconv"
)

// 1. 안드로이드의 FeedItem 데이터 클래스와 '정확히' 일치하는 Go 구조체
// (json:"key")는 JSON으로 변환될 때의 필드 이름을 지정합니다.
type FeedItem struct {
	ID                  string `json:"id"`
	UserName            string `json:"user_name"`
	UserProfileImageUrl string `json:"user_profile_image_url"`
	PostImageUrl        string `json:"post_image_url"`
	Content             string `json:"content"`
	LikesCount          int    `json:"likes_count"`
}

// ⭐️ [신규] 2. DeliveryItem Go 구조체
// (안드로이드 data class와 매핑)
type DeliveryItem struct {
	ID                     string `json:"id"`
	StoreName              string `json:"store_name"`
	StoreImageUrl          string `json:"store_image_url"`
	EstimatedTimeInMinutes int    `json:"estimated_time_in_minutes"`
	Status                 string `json:"status"`
}

// ⭐️ [신규] 3. ShortsItem Go 구조체
type ShortsItem struct {
	ID        string `json:"id"`
	StoreName string `json:"store_name"` // Feed/Delivery와 매핑 일치
	StoreID   string `json:"store_id"`
	VideoURL  string `json:"video_url"` // (지금은 안 쓰지만 추가)
}

// 2. /feed 경로로 요청이 오면 실행될 핸들러 함수
func feedHandler(w http.ResponseWriter, r *http.Request) {
	// White Box: Go 서버가 요청을 받았음을 터미널에 출력
	log.Println("안드로이드로부터 /feed 요청 수신!")

	// 3. '반경 확장' 로직 (14단계와 유사하게)
	// (실제로는 DB에서 가져와야 하지만, 지금은 Go로 가짜 데이터를 생성합니다)
	radiusQuery := r.URL.Query().Get("radius") // (예: /feed?radius=5)
	radius, _ := strconv.Atoi(radiusQuery)
	if radius == 0 {
		radius = 5 // 기본값
	}

	var feedItems []FeedItem
	itemCount := 20
	prefix := fmt.Sprintf("Go-%dkm", radius)

	for i := 1; i <= itemCount; i++ {
		id := (radius * 1000) + i
		feedItems = append(feedItems, FeedItem{
			ID:                  fmt.Sprintf("go_uuid_%d", id),
			UserName:            fmt.Sprintf("%s_user_%d", prefix, i),
			UserProfileImageUrl: fmt.Sprintf("https://picsum.photos/seed/user_%d/100/100", id),
			PostImageUrl:        fmt.Sprintf("https://picsum.photos/seed/%d/300/300", id),
			Content:             fmt.Sprintf("이것은 Go 서버가 제공하는 %dkm 반경의 %d번째 피드입니다.", radius, i),
			LikesCount:          (i * 5) % 100,
		})
	}

	// 4. Content-Type을 JSON으로 설정 (필수)
	w.Header().Set("Content-Type", "application/json")

	// 5. Go 구조체 슬라이스(feedItems)를 JSON으로 변환하여 응답
	json.NewEncoder(w).Encode(feedItems)
}

// ⭐️ [신규] 4. /delivery 경로 핸들러
func deliveryHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("안드로이드로부터 /delivery 요청 수신!")

	var deliveryItems []DeliveryItem
	itemCount := 30 // 배달은 30개 고정 (페이징 없음)

	for i := 1; i <= itemCount; i++ {
		deliveryItems = append(deliveryItems, DeliveryItem{
			ID:                     fmt.Sprintf("go_delivery_uuid_%d", i),
			StoreName:              fmt.Sprintf("Go-맛있는 가게 #%d", i),
			StoreImageUrl:          fmt.Sprintf("https://picsum.photos/seed/store_%d/200/200", i),
			EstimatedTimeInMinutes: (i*3)%60 + 10,
			Status:                 "배달중",
		})
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(deliveryItems)
}

// ⭐️ [신규] 6. /shorts 경로 핸들러
func shortsHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("안드로이드로부터 /shorts 요청 수신!")

	// (요구사항: 쇼츠는 거리와 무관. 단순 목록 반환)
	var shortsItems []ShortsItem
	itemCount := 20

	for i := 1; i <= itemCount; i++ {
		shortsItems = append(shortsItems, ShortsItem{
			ID:        fmt.Sprintf("go_shorts_uuid_%d", i),
			StoreName: fmt.Sprintf("Go-쇼츠 가게 #%d", i),
			StoreID:   fmt.Sprintf("store_%d", i),
			VideoURL:  "", // (지금은 비워둠)
		})
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(shortsItems)
}

func main() {
	// 7. [수정] 라우트 등록
	http.HandleFunc("/feed", feedHandler)
	http.HandleFunc("/delivery", deliveryHandler)
	http.HandleFunc("/shorts", shortsHandler) // ⭐️ [신규] /shorts 라우트 추가

	fmt.Println("Go 백엔드 서버가 http://localhost:8080 에서 실행 중입니다...")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
