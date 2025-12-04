# 1. 빌드 단계 (Builder Stage)
FROM golang:1.21-alpine AS builder

# 필수 패키지 설치 (SQLite 빌드용 gcc, musl-dev 필요)
RUN apk add --no-cache gcc musl-dev

WORKDIR /app

# 의존성 파일 복사 및 다운로드
COPY go.mod go.sum ./
RUN go mod download

# 소스 코드 복사 및 빌드
COPY . .
# CGO_ENABLED=1은 SQLite 때문에 필수입니다.
RUN CGO_ENABLED=1 GOOS=linux go build -o delivery-server main.go

# 2. 실행 단계 (Runner Stage) - 가벼운 이미지 사용
FROM alpine:latest

WORKDIR /root/

# 빌드된 실행 파일만 가져오기
COPY --from=builder /app/delivery-server .

# DB 파일이 저장될 폴더 생성 (볼륨 마운트용)
RUN mkdir -p /root/data

# 포트 노출
EXPOSE 8080

# 실행 명령
CMD ["./delivery-server"]