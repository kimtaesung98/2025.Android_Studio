package handler

import (
	"delivery-service/internal/domain"
	"delivery-service/pkg/database"
	"net/http"

	"github.com/gin-gonic/gin"
)

// 가게 등록
func CreateStore(c *gin.Context) {
	var store domain.Store
	if err := c.ShouldBindJSON(&store); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	database.DB.Create(&store)
	c.JSON(http.StatusOK, gin.H{"message": "가게 오픈 완료!", "store": store})
}

// 메뉴 등록 (옵션 포함)
func CreateMenu(c *gin.Context) {
	var menu domain.Menu
	if err := c.ShouldBindJSON(&menu); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	if err := database.DB.Create(&menu).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "메뉴 등록 실패"})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "메뉴 등록 완료!", "menu": menu})
}

// 메뉴 조회
func GetMenus(c *gin.Context) {
	storeID := c.Param("store_id")
	var menus []domain.Menu
	database.DB.Preload("OptionGroups.Options").Where("store_id = ?", storeID).Find(&menus)
	c.JSON(http.StatusOK, gin.H{"menus": menus})
}
