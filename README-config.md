# Cấu hình ứng dụng

## Cài đặt cấu hình

Các file cấu hình đã được ẩn khỏi git repository để bảo mật. Khi clone project, bạn cần tạo các file cấu hình dựa trên các file mẫu:

1. Sao chép từ file mẫu:
```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
cp src/main/resources/application-prod.yml.example src/main/resources/application-prod.yml
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

2. Cập nhật các thông tin nhạy cảm trong file cấu hình của bạn:
   - Database connection
   - JWT secrets
   - Email credentials
   - VNPay credentials

## Cấu hình VNPAY

Để VNPAY chuyển hướng về trang local sau khi thanh toán, cần cấu hình giá trị `frontendUrl` trong file application.yml:

```yaml
vnpay:
  # Các cấu hình khác...
  frontendUrl: ${FRONTEND_URL:http://localhost:3000}
```

Khi chạy ứng dụng ở môi trường local, bạn có thể:

1. Đảm bảo không thiết lập biến môi trường `FRONTEND_URL`
2. Hoặc thiết lập biến môi trường rõ ràng: `FRONTEND_URL=http://localhost:3000`
3. Hoặc thêm tham số khi chạy ứng dụng: `-DFRONTEND_URL=http://localhost:3000`

Đảm bảo không sử dụng profile prod khi phát triển local.

## Biến Môi Trường

Các biến môi trường quan trọng:

- `FRONTEND_URL`: URL của frontend, mặc định là `http://localhost:3000`
- `VNPAY_TMN_CODE`: Mã TMN của VNPAY
- `VNPAY_HASH_SECRET`: Secret key của VNPAY
- `VNPAY_RETURN_URL`: URL callback của VNPAY
- `MYSQL_URL`, `MYSQL_USERNAME`, `MYSQL_PASSWORD`: Cấu hình database
- `MAIL_USERNAME`, `MAIL_PASSWORD`: Cấu hình email
