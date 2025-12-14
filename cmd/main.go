package main

import (
	"delivery-service/internal/handler"    // 핸들러들이 있는 패키지
	"delivery-service/internal/middleware" // 인증 미들웨어
	"delivery-service/pkg/database"        // DB 연결

	"github.com/gin-gonic/gin"
)

func main() {
	// 1. 데이터베이스 연결
	database.Connect()

	// 2. Gin 웹 프레임워크 생성
	r := gin.Default()

	// 3. 기본 헬스 체크 경로
	r.GET("/ping", func(c *gin.Context) {
		c.JSON(200, gin.H{"message": "pong"})
	})

	// 4. 인증 (로그인/회원가입) - 토큰 필요 없음
	auth := r.Group("/auth")
	{
		auth.POST("/register", handler.Register)
		auth.POST("/login", handler.Login)
	}

	// 5. 가게 및 메뉴 등록 (테스트 편의를 위해 일단 토큰 검사 제외)
	r.POST("/stores", handler.CreateStore)
	r.POST("/menus", handler.CreateMenu)
	r.GET("/stores/:store_id/menus", handler.GetMenus)

	// 6. 인증이 필요한 기능들 (토큰 필수)
	authorized := r.Group("/")
	authorized.Use(middleware.AuthMiddleware())
	{
		// [고객] 주문하기
		authorized.POST("/orders", handler.CreateOrder)

		// [점주] 가게 주문 조회 및 상태 변경
		authorized.GET("/stores/:store_id/orders", handler.GetStoreOrders)
		authorized.PATCH("/orders/:order_id/status", handler.UpdateOrderStatus)
	}

	// 7. 서버 실행 (8080 포트)
	r.Run(":8080")
}
