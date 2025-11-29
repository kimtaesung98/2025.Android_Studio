package main

import (
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
)

// --- 1. Data Models (Android 앱과 필드명 일치) ---

type Store struct {
	ID            string `json:"id"`
	Name          string `json:"name"`
	Rating        string `json:"rating"` // JSON number as string or double handled by client
	DeliveryTime  string `json:"deliveryTime"`
	MinOrderPrice int    `json:"minOrderPrice"`
	ImageURL      string `json:"imageUrl"`
}

type Order struct {
	ID         string   `json:"id"`
	StoreName  string   `json:"storeName"`
	Items      []string `json:"items"`
	TotalPrice int      `json:"totalPrice"`
	Status     string   `json:"status"` // PENDING, COOKING, etc.
	Date       string   `json:"date"`
}

// 요청/응답용 DTO
type OrderRequest struct {
	StoreID    string   `json:"storeId"`
	Items      []string `json:"items"`
	TotalPrice int      `json:"totalPrice"`
}

type OrderResponse struct {
	Success bool   `json:"success"`
	OrderID string `json:"orderId"`
	Message string `json:"message"`
}

type StatusUpdateRequest struct {
	Status string `json:"status"`
}

// --- 2. Mock Data (In-Memory Database) ---

var mockStores = []Store{
	{ID: "1", Name: "Burger King", Rating: "4.8", DeliveryTime: "20-30 min", MinOrderPrice: 15000, ImageURL: ""},
	{ID: "2", Name: "Pizza Hut", Rating: "4.5", DeliveryTime: "40-50 min", MinOrderPrice: 20000, ImageURL: ""},
	{ID: "3", Name: "Kyochon Chicken", Rating: "4.9", DeliveryTime: "30-40 min", MinOrderPrice: 18000, ImageURL: ""},
	{ID: "4", Name: "Starbucks", Rating: "4.7", DeliveryTime: "10-20 min", MinOrderPrice: 12000, ImageURL: ""},
}

var mockOrders = []Order{
	{ID: "101", StoreName: "Burger King", Items: []string{"Whopper Set"}, TotalPrice: 8900, Status: "COOKING", Date: "2023-10-25"},
	{ID: "102", StoreName: "Pizza Hut", Items: []string{"Cheese Pizza", "Coke"}, TotalPrice: 24000, Status: "COMPLETED", Date: "2023-10-24"},
}

func main() {
	r := gin.Default()

	// CORS 설정 (모든 출처 허용 - 개발용)
	r.Use(cors.Default())

	// --- 3. API Endpoints ---

	// [GET] 매장 목록 조회
	r.GET("/stores", func(c *gin.Context) {
		fmt.Printf("[GET] /stores - Sending %d stores\n", len(mockStores))
		c.JSON(http.StatusOK, mockStores)
	})

	// [POST] 주문 생성 (고객)
	r.POST("/orders", func(c *gin.Context) {
		var req OrderRequest
		if err := c.BindJSON(&req); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request"})
			return
		}

		fmt.Printf("[POST] /orders - New Order for StoreID: %s\n", req.StoreID)

		// 매장 이름 찾기
		storeName := "Unknown Store"
		for _, s := range mockStores {
			if s.ID == req.StoreID {
				storeName = s.Name
				break
			}
		}

		// 새 주문 생성
		newID := strconv.FormatInt(time.Now().Unix(), 10)
		newOrder := Order{
			ID:         newID,
			StoreName:  storeName,
			Items:      req.Items,
			TotalPrice: req.TotalPrice,
			Status:     "PENDING",
			Date:       time.Now().Format("2006-01-02"),
		}

		// 리스트 맨 앞에 추가 (최신순)
		mockOrders = append([]Order{newOrder}, mockOrders...)

		resp := OrderResponse{
			Success: true,
			OrderID: newID,
			Message: "Order placed successfully",
		}
		c.JSON(http.StatusOK, resp)
	})

	// [GET] 내 주문 내역 (고객)
	r.GET("/orders/my", func(c *gin.Context) {
		fmt.Printf("[GET] /orders/my - Sending %d orders\n", len(mockOrders))
		c.JSON(http.StatusOK, mockOrders)
	})

	// [GET] 들어온 주문 목록 (점주)
	r.GET("/owner/orders", func(c *gin.Context) {
		fmt.Printf("[GET] /owner/orders - Owner checking orders\n")
		c.JSON(http.StatusOK, mockOrders)
	})

	// [PUT] 주문 상태 변경 (점주)
	r.PUT("/owner/orders/:orderId/status", func(c *gin.Context) {
		orderID := c.Param("orderId")
		var req StatusUpdateRequest
		if err := c.BindJSON(&req); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid body"})
			return
		}

		fmt.Printf("[PUT] Status Update - Order %s to %s\n", orderID, req.Status)

		found := false
		for i, order := range mockOrders {
			if order.ID == orderID {
				mockOrders[i].Status = req.Status
				found = true
				break
			}
		}

		if found {
			c.JSON(http.StatusOK, OrderResponse{
				Success: true,
				OrderID: orderID,
				Message: "Status updated to " + req.Status,
			})
		} else {
			c.JSON(http.StatusNotFound, gin.H{"success": false, "message": "Order not found"})
		}
	})

	// --- 4. Server Start ---
	fmt.Println("🚀 Delivery App Go Server running at http://localhost:8080")
	r.Run(":8080")
}
