package domain

import (
	"time"

	"gorm.io/gorm"
)

// User: 고객 및 점주 정보
type User struct {
	ID        uint   `gorm:"primaryKey"`
	Username  string `gorm:"unique;not null"`
	Password  string `gorm:"not null"`
	Role      string `gorm:"default:'customer'"` // 'customer' or 'owner'
	CreatedAt time.Time
	UpdatedAt time.Time
	DeletedAt gorm.DeletedAt `gorm:"index"`
}

type Store struct {
	gorm.Model
	OwnerID     uint   `json:"owner_id"` // 가게 주인의 User ID
	Name        string `json:"name"`
	Description string `json:"description"`
}

type Menu struct {
	gorm.Model
	StoreID   uint   `json:"store_id"`
	Name      string `json:"name"`
	Price     int    `json:"price"`
	IsSoldOut bool   `json:"is_sold_out"`
	// [중요] 메뉴는 여러 개의 옵션 그룹을 가집니다 (1:N)
	OptionGroups []OptionGroup `json:"option_groups" gorm:"foreignKey:MenuID"`
}

type OptionGroup struct {
	gorm.Model
	MenuID      uint   `json:"menu_id"`
	Name        string `json:"name"`         // 예: 맵기 조절, 토핑 추가
	IsMandatory bool   `json:"is_mandatory"` // 필수 선택 여부
	MaxSelect   int    `json:"max_select"`   // 최대 선택 가능 수
	// [중요] 그룹은 여러 개의 세부 옵션을 가집니다 (1:N)
	Options []Option `json:"options" gorm:"foreignKey:GroupID"`
}

type Option struct {
	gorm.Model
	GroupID    uint   `json:"group_id"`
	Name       string `json:"name"`        // 예: 아주 매운맛, 치즈
	ExtraPrice int    `json:"extra_price"` // +500원
}
