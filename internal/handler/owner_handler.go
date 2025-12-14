package handler

import (
	"delivery-service/internal/domain"
	"delivery-service/pkg/database"
	"net/http"

	"github.com/gin-gonic/gin"
)

// 1. 점주용: 내 가게에 들어온 주문 목록 보기
// GET /stores/:store_id/orders
func GetStoreOrders(c *gin.Context) {
	storeID := c.Param("store_id")

	// 토큰에서 현재 접속자(점주) ID 확인
	userID, _ := c.Get("userID")

	// 보안 체크: 실제로는 이 가게의 주인이 맞는지 확인하는 로직이 필요합니다.
	// (여기서는 생략하고 기능 구현에 집중합니다)

	var orders []domain.Order

	// 주문 정보와 함께 상세 메뉴(OrderItems)까지 한 번에 가져옵니다 (Preload)
	// 최신 주문이 위로 오도록 내림차순 정렬 (order by id desc)
	result := database.DB.Preload("Items").Where("store_id = ?", storeID).Order("id desc").Find(&orders)

	if result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "주문 목록을 불러오지 못했습니다"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"owner_id": userID,
		"count":    len(orders),
		"orders":   orders,
	})
}

// 상태 변경을 위한 입력 구조체
type UpdateStatusInput struct {
	Status string `json:"status" binding:"required"` // ACCEPTED, COOKING, DELIVERING, COMPLETED
}

// 2. 점주용: 주문 상태 변경하기 (접수, 배달시작 등)
// PATCH /orders/:order_id/status
func UpdateOrderStatus(c *gin.Context) {
	orderID := c.Param("order_id")

	// 입력값 확인 (변경할 상태)
	var input UpdateStatusInput
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// 변경할 주문 찾기
	var order domain.Order
	if err := database.DB.First(&order, orderID).Error; err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "주문을 찾을 수 없습니다"})
		return
	}

	// 상태 업데이트 실행
	// Model(&order)를 사용하면 updated_at 시간이 자동으로 갱신됩니다.
	database.DB.Model(&order).Update("status", input.Status)

	c.JSON(http.StatusOK, gin.H{
		"message":    "주문 상태가 변경되었습니다.",
		"order_id":   order.ID,
		"new_status": input.Status,
	})
}
