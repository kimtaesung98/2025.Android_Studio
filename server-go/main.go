package main

import (
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
)

// --- 1. Data Models ---

type Store struct {
	ID            string `json:"id"`
	Name          string `json:"name"`
	Rating        string `json:"rating"`
	DeliveryTime  string `json:"deliveryTime"`
	MinOrderPrice int    `json:"minOrderPrice"`
	ImageURL      string `json:"imageUrl"`
}

type MenuItem struct {
	ID          string `json:"id"`
	StoreID     string `json:"storeId"`
	Name        string `json:"name"`
	Price       int    `json:"price"`
	Description string `json:"description"`
}

type Order struct {
	ID         string   `json:"id"`
	StoreName  string   `json:"storeName"`
	Items      []string `json:"items"`
	TotalPrice int      `json:"totalPrice"`
	Status     string   `json:"status"`
	Date       string   `json:"date"`
}

// DTOs
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

// --- 2. Mock Data ---

var mockStores = []Store{
	{ID: "1", Name: "Burger King", Rating: "4.8", DeliveryTime: "20-30 min", MinOrderPrice: 15000, ImageURL: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=500&q=60"},
	{ID: "2", Name: "Pizza Hut", Rating: "4.5", DeliveryTime: "40-50 min", MinOrderPrice: 20000, ImageURL: "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?auto=format&fit=crop&w=500&q=60"},
}

// 초기 메뉴 데이터
var mockMenus = []MenuItem{
	{ID: "m1", StoreID: "1", Name: "Whopper Set", Price: 8900, Description: "Flame-grilled beef patty"},
	{ID: "m2", StoreID: "1", Name: "Cheese Fries", Price: 3500, Description: "Crispy fries with cheese"},
	{ID: "m3", StoreID: "2", Name: "Cheese Pizza", Price: 18000, Description: "Rich mozzarella cheese"},
}

var mockOrders = []Order{}

func main() {
	r := gin.Default()
	r.Use(cors.Default())

	// [GET] 매장 목록
	r.GET("/stores", func(c *gin.Context) {
		c.JSON(http.StatusOK, mockStores)
	})

	// [GET] 특정 매장의 메뉴 목록
	r.GET("/stores/:storeId/menus", func(c *gin.Context) {
		storeID := c.Param("storeId")
		var storeMenus []MenuItem
		for _, m := range mockMenus {
			if m.StoreID == storeID {
				storeMenus = append(storeMenus, m)
			}
		}
		fmt.Printf("[GET] Menus for Store %s -> count: %d\n", storeID, len(storeMenus))
		c.JSON(http.StatusOK, storeMenus)
	})

	// [POST] 메뉴 추가 (점주용)
	r.POST("/menus", func(c *gin.Context) {
		var newMenu MenuItem
		if err := c.BindJSON(&newMenu); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid data"})
			return
		}
		newMenu.ID = strconv.FormatInt(time.Now().UnixNano(), 10) // Unique ID
		mockMenus = append(mockMenus, newMenu)

		fmt.Printf("[POST] New Menu Added: %s (%d won)\n", newMenu.Name, newMenu.Price)
		c.JSON(http.StatusOK, gin.H{"success": true, "menu": newMenu})
	})

	// [POST] 주문 접수
	r.POST("/orders", func(c *gin.Context) {
		var req OrderRequest
		if err := c.BindJSON(&req); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request"})
			return
		}

		storeName := "Unknown Store"
		for _, s := range mockStores {
			if s.ID == req.StoreID {
				storeName = s.Name
				break
			}
		}

		newOrder := Order{
			ID:         strconv.FormatInt(time.Now().Unix(), 10),
			StoreName:  storeName,
			Items:      req.Items,
			TotalPrice: req.TotalPrice,
			Status:     "PENDING",
			Date:       time.Now().Format("2006-01-02 15:04"),
		}
		mockOrders = append([]Order{newOrder}, mockOrders...) // Prepend

		fmt.Printf("[POST] Order Received: %s\n", newOrder.ID)
		c.JSON(http.StatusOK, OrderResponse{Success: true, OrderID: newOrder.ID, Message: "Order Placed"})
	})

	// [GET] 점주용 주문 목록
	r.GET("/owner/orders", func(c *gin.Context) {
		c.JSON(http.StatusOK, mockOrders)
	})

	// [PUT] 주문 상태 변경
	r.PUT("/owner/orders/:orderId/status", func(c *gin.Context) {
		orderID := c.Param("orderId")
		var req StatusUpdateRequest
		if err := c.BindJSON(&req); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid body"})
			return
		}
		for i, o := range mockOrders {
			if o.ID == orderID {
				mockOrders[i].Status = req.Status
				c.JSON(http.StatusOK, OrderResponse{Success: true, OrderID: orderID, Message: "Updated"})
				return
			}
		}
		c.JSON(http.StatusNotFound, gin.H{"error": "Order not found"})
	})

	fmt.Println("🚀 Delivery Server running at http://localhost:8080")
	r.Run(":8080")
}
