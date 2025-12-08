# 🟢 [수정] 1.23 -> 1.24 (최신 안정 버전)
FROM golang:1.24-alpine AS builder

# SQLite 빌드를 위한 필수 설정
ENV CGO_ENABLED=1
RUN apk add --no-cache gcc musl-dev

WORKDIR /app

# go.sum이 있으면 같이 복사
COPY go.mod go.sum* ./
RUN go mod download

COPY . .
RUN go build -o main .

FROM alpine:latest
WORKDIR /app
RUN apk add --no-cache sqlite-libs
COPY --from=builder /app/main .
EXPOSE 8080
CMD ["./main"]