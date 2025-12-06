package main

import (
	"fmt"
	"log"
	"net/http"
	"strconv"
	"strings"
	"time"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5" // 추가
	"github.com/gorilla/websocket"
	"golang.org/x/crypto/bcrypt" // 추가
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

// [수정] User 모델에 Address 추가
type User struct {
	ID       string `json:"id" gorm:"primaryKey"`
	Email    string `json:"email" gorm:"unique"`
	Password string `json:"password"`
	Name     string `json:"name"`
	Role     string `json:"role"`
	Address  string `json:"address"` // [추가]
}

// [수정] DTO 업데이트
type AuthRequest struct {
	Email    string `json:"email"`
	Password string `json:"password"`
	Name     string `json:"name"`
	Role     string `json:"role"`
	Address  string `json:"address"` // [추가] 회원가입 시 받음
}

// JWT Secret Key (실무에선 환경변수로 관리)
var jwtKey = []byte("my_secret_key")

// Claims 구조체
type Claims struct {
	UserID string `json:"userId"`
	Role   string `json:"role"`
	jwt.RegisteredClaims
}

type Store struct {
	ID            string `json:"id" gorm:"primaryKey"`
	Name          string `json:"name"`
	Rating        string `json:"rating"`
	DeliveryTime  string `json:"deliveryTime"`
	MinOrderPrice int    `json:"minOrderPrice"`
	ImageURL      string `json:"imageUrl"`
}

type MenuItem struct {
	ID          string `json:"id" gorm:"primaryKey"`
	StoreID     string `json:"storeId"`
	Name        string `json:"name"`
	Price       int    `json:"price"`
	Description string `json:"description"`
	ImageURL    string `json:"imageUrl"`
}

// [수정] Order 모델에 DeliveryAddress 추가
type Order struct {
	ID              string   `json:"id" gorm:"primaryKey"`
	StoreName       string   `json:"storeName"`
	ItemsJson       string   `json:"-"`
	Items           []string `json:"items" gorm:"-"`
	TotalPrice      int      `json:"totalPrice"`
	Status          string   `json:"status"`
	Date            string   `json:"date"`
	DeliveryAddress string   `json:"deliveryAddress"` // [추가]
}

type OrderRequest struct {
	StoreID         string   `json:"storeId"`
	Items           []string `json:"items"`
	TotalPrice      int      `json:"totalPrice"`
	DeliveryAddress string   `json:"deliveryAddress"` // [추가] 주문 시 받음
}

type StatusUpdateRequest struct {
	Status string `json:"status"`
}

// [추가] 통계 응답용 구조체
type DashboardStats struct {
	TotalSales       int   `json:"totalSales"`       // 총 매출액
	TotalOrders      int64 `json:"totalOrders"`      // 총 주문 수
	PendingOrders    int64 `json:"pendingOrders"`    // 대기 중인 주문
	ProcessingOrders int64 `json:"processingOrders"` // 조리/배달 중인 주문
}

var db *gorm.DB

// --- 2. WebSocket Setup ---

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool { return true },
}
var clients = make(map[*websocket.Conn]bool) // 접속된 클라이언트들
var broadcast = make(chan interface{})       // 메시지 방송 채널

func handleConnections(c *gin.Context) {
	ws, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		log.Fatal(err)
	}
	defer ws.Close()
	clients[ws] = true
	fmt.Println("✅ New Client Connected via WebSocket")

	for {
		var msg interface{}
		err := ws.ReadJSON(&msg) // Keep connection alive
		if err != nil {
			delete(clients, ws)
			break
		}
	}
}

func handleMessages() {
	for {
		msg := <-broadcast
		for client := range clients {
			err := client.WriteJSON(msg)
			if err != nil {
				client.Close()
				delete(clients, client)
			}
		}
	}
}

func authMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Authorization header required"})
			c.Abort()
			return
		}

		// "Bearer <token>" 형식 파싱
		tokenString := strings.TrimPrefix(authHeader, "Bearer ")
		if tokenString == authHeader {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid token format"})
			c.Abort()
			return
		}

		claims := &Claims{}
		token, err := jwt.ParseWithClaims(tokenString, claims, func(token *jwt.Token) (interface{}, error) {
			return jwtKey, nil
		})

		if err != nil || !token.Valid {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid or expired token"})
			c.Abort()
			return
		}

		// 토큰에서 꺼낸 사용자 정보를 컨텍스트에 저장 (API에서 쓸 수 있게)
		c.Set("userId", claims.UserID)
		c.Set("role", claims.Role)

		c.Next() // 통과!
	}
}

// --- 3. Main Function ---

func main() {
	// DB 초기화 (SQLite)
	var err error
	db, err = gorm.Open(sqlite.Open("delivery.db"), &gorm.Config{})
	if err != nil {
		panic("failed to connect database")
	}

	// 테이블 자동 생성 (Auto Migrate)
	db.AutoMigrate(&Store{}, &MenuItem{}, &Order{}, &User{})
	seedDatabase() // 초기 데이터 주입

	// 소켓 메시지 처리 고루틴 시작
	go handleMessages()

	r := gin.Default()
	r.Use(cors.Default())

	// --- WebSocket Endpoint ---
	r.GET("/ws", handleConnections)

	// --- REST APIs ---

	r.GET("/stores", func(c *gin.Context) {
		var stores []Store
		db.Find(&stores)
		c.JSON(http.StatusOK, stores)
	})

	r.GET("/stores/:storeId/menus", func(c *gin.Context) {
		var menus []MenuItem
		db.Where("store_id = ?", c.Param("storeId")).Find(&menus)
		c.JSON(http.StatusOK, menus)
	})

	// 회원가입
	r.POST("/auth/register", func(c *gin.Context) {
		var req AuthRequest
		if err := c.BindJSON(&req); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid body"})
			return
		}

		// 비밀번호 해싱
		hashedPassword, _ := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)

		user := User{
			ID:       strconv.FormatInt(time.Now().UnixNano(), 10),
			Email:    req.Email,
			Password: string(hashedPassword),
			Name:     req.Name,
			Role:     req.Role,
			Address:  req.Address, // [추가] DB에 주소 저장
		}

		if result := db.Create(&user); result.Error != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Email already exists"})
			return
		}
		c.JSON(http.StatusOK, gin.H{"success": true, "message": "User created"})
	})

	// 로그인
	r.POST("/auth/login", func(c *gin.Context) {
		var req AuthRequest
		if err := c.BindJSON(&req); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid body"})
			return
		}

		var user User
		if err := db.Where("email = ?", req.Email).First(&user).Error; err != nil {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
			return
		}

		// 비밀번호 검증
		if err := bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(req.Password)); err != nil {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Wrong password"})
			return
		}

		// 토큰 생성
		expirationTime := time.Now().Add(24 * time.Hour)
		claims := &Claims{
			UserID: user.ID,
			Role:   user.Role,
			RegisteredClaims: jwt.RegisteredClaims{
				ExpiresAt: jwt.NewNumericDate(expirationTime),
			},
		}
		token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
		tokenString, _ := token.SignedString(jwtKey)

		c.JSON(http.StatusOK, gin.H{
			"success": true,
			"token":   tokenString,
			"role":    user.Role,
			"name":    user.Name,
			"address": user.Address, // [추가] 로그인 시 주소 반환
		})
	})

	// --- Protected APIs (로그인 필수) ---
	// authorized 그룹을 만들어 미들웨어 적용
	authorized := r.Group("/")
	authorized.Use(authMiddleware())
	{
		// 메뉴 추가 (점주만 가능하게 하려면 여기서 role 체크 로직 추가 가능)
		authorized.POST("/menus", func(c *gin.Context) {
			var menu MenuItem
			if err := c.BindJSON(&menu); err != nil {
				return
			}
			menu.ID = strconv.FormatInt(time.Now().UnixNano(), 10)
			db.Create(&menu)
			c.JSON(http.StatusOK, gin.H{"success": true})
		})

		authorized.POST("/orders", func(c *gin.Context) {
			var req OrderRequest
			if err := c.BindJSON(&req); err != nil {
				return
			}

			var store Store
			db.First(&store, "id = ?", req.StoreID)

			// 아이템 리스트를 DB에 넣기 위해 문자열로 변환 (간이 구현)
			itemsStr := fmt.Sprintf("%v", req.Items)

			newOrder := Order{
				ID:              strconv.FormatInt(time.Now().Unix(), 10),
				StoreName:       store.Name,
				ItemsJson:       itemsStr, // 실제로는 별도 테이블이나 JSON 컬럼 추천
				Items:           req.Items,
				TotalPrice:      req.TotalPrice,
				Status:          "PENDING",
				Date:            time.Now().Format("2006-01-02 15:04"),
				DeliveryAddress: req.DeliveryAddress, // [추가] 배달 주소 저장
			}
			db.Create(&newOrder)

			// ⭐ [Real-time] 새 주문 알림 방송!
			broadcast <- gin.H{"type": "NEW_ORDER", "orderId": newOrder.ID}

			c.JSON(http.StatusOK, gin.H{"success": true, "orderId": newOrder.ID})
		})

		// 내 주문 보기 / 점주 주문 보기
		authorized.GET("/owner/orders", func(c *gin.Context) {
			var orders []Order
			db.Order("date desc").Find(&orders)

			// [추가] DB에 저장된 문자열([item1, item2])을 파싱해서 Items 필드에 채워넣기
			// 간단하게 JSON 파싱 대신 문자열 그대로를 리스트 하나에 담아서 보내거나,
			// 제대로 하려면 json.Unmarshal을 써야 함.
			// 여기서는 UI 테스트를 위해 임시로 처리:
			for i := range orders {
				// ItemsJson이 "[A, B]" 형태의 문자열이라면,
				// 클라이언트가 List<String>으로 받기 위해선 가공이 필요함.
				// 편의상 ItemsJson 내용을 그대로 Items 첫 번째 요소로 넣음 (또는 파싱 로직 구현)
				if orders[i].ItemsJson != "" {
					orders[i].Items = []string{orders[i].ItemsJson}
				}
			}

			c.JSON(http.StatusOK, orders)
		})

		authorized.PUT("/owner/orders/:orderId/status", func(c *gin.Context) {
			orderID := c.Param("orderId")
			var req StatusUpdateRequest
			c.BindJSON(&req)

			var order Order
			if result := db.First(&order, "id = ?", orderID); result.Error == nil {
				order.Status = req.Status
				db.Save(&order)

				// ⭐ [Real-time] 상태 변경 알림 방송!
				broadcast <- gin.H{"type": "STATUS_UPDATE", "orderId": orderID, "status": req.Status}

				c.JSON(http.StatusOK, gin.H{"success": true})
			} else {
				c.Status(404)
			}
		})

		// [추가] 점주용 대시보드 통계 API
		authorized.GET("/owner/dashboard", func(c *gin.Context) {
			var stats DashboardStats

			// 1. 총 매출 (상태가 DELIVERED인 주문의 가격 합계)
			db.Model(&Order{}).Where("status = ?", "DELIVERED").Select("COALESCE(SUM(total_price), 0)").Scan(&stats.TotalSales)

			// 2. 전체 주문 수
			db.Model(&Order{}).Count(&stats.TotalOrders)

			// 3. 대기 중인 주문 수 (PENDING)
			db.Model(&Order{}).Where("status = ?", "PENDING").Count(&stats.PendingOrders)

			// 4. 처리 중인 주문 수 (수락됨 ~ 배달 중)
			db.Model(&Order{}).Where("status IN ?", []string{"ACCEPTED", "COOKING", "READY_FOR_PICKUP", "ON_DELIVERY"}).Count(&stats.ProcessingOrders)

			c.JSON(200, stats)
		})
	}

	fmt.Println("🚀 Real-time DB Server running at :8080")
	r.Run(":8080")
}

func seedDatabase() {
	var count int64
	db.Model(&Store{}).Count(&count)
	if count == 0 {
		db.Create(&Store{ID: "1", Name: "Burger King", Rating: "4.8", DeliveryTime: "25 min", MinOrderPrice: 15000, ImageURL: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500"})
		db.Create(&Store{ID: "2", Name: "Pizza Hut", Rating: "4.5", DeliveryTime: "40 min", MinOrderPrice: 20000, ImageURL: "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?w=500"})
		// 초기 메뉴
		db.Create(&MenuItem{ID: "m1", StoreID: "1", Name: "Whopper", Price: 8000, Description: "Tasty", ImageURL: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=200"})
	}
}
