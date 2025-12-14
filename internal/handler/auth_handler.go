package handler

import (
	"delivery-service/internal/domain"
	"delivery-service/pkg/database"
	"delivery-service/pkg/utils"
	"net/http"

	"github.com/gin-gonic/gin"
)

// 회원가입 입력 데이터 구조
type RegisterInput struct {
	Username string `json:"username" binding:"required"`
	Password string `json:"password" binding:"required"`
	Role     string `json:"role" binding:"required"` // 'customer' 또는 'owner'
}

// 회원가입 (Register)
func Register(c *gin.Context) {
	var input RegisterInput
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// 1. 비밀번호 암호화
	hashedPassword, _ := utils.HashPassword(input.Password)

	// 2. DB 저장할 유저 객체 생성
	user := domain.User{
		Username: input.Username,
		Password: hashedPassword,
		Role:     input.Role,
	}

	// 3. DB에 저장
	if err := database.DB.Create(&user).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "회원가입 실패 (중복된 ID일 수 있습니다)"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "회원가입 성공!", "user_id": user.ID})
}

// 로그인 입력 데이터 구조
type LoginInput struct {
	Username string `json:"username" binding:"required"`
	Password string `json:"password" binding:"required"`
}

// 로그인 (Login)
func Login(c *gin.Context) {
	var input LoginInput
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// 1. 유저 조회
	var user domain.User
	if err := database.DB.Where("username = ?", input.Username).First(&user).Error; err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "존재하지 않는 사용자입니다."})
		return
	}

	// 2. 비밀번호 확인
	if !utils.CheckPassword(user.Password, input.Password) {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "비밀번호가 틀렸습니다."})
		return
	}

	// 3. 토큰 발급
	token, _ := utils.GenerateToken(user.ID, user.Role)

	c.JSON(http.StatusOK, gin.H{
		"message": "로그인 성공",
		"token":   token,
		"role":    user.Role,
	})
}
