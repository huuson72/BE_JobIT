# JobIT Backend

## Environment Setup

Dự án này sử dụng biến môi trường để lưu trữ thông tin nhạy cảm như thông tin đăng nhập cơ sở dữ liệu, API keys, và các thông tin cấu hình khác. Để thiết lập môi trường đúng cách:

1. Tạo file `.env` trong thư mục gốc của dự án
2. Sao chép nội dung từ `.env.example` vào file `.env` của bạn
3. Thay thế các giá trị mẫu bằng giá trị cấu hình thực tế của bạn

```bash
# Trên Linux/Mac
cp .env.example .env

# Trên Windows
copy .env.example .env
```

## Cấu hình Application Files

Dự án này sử dụng các file cấu hình Spring Boot. Các file gốc đã được thêm vào `.gitignore` để bảo vệ thông tin nhạy cảm. Để thiết lập:

1. Sao chép các file mẫu để tạo file cấu hình thực:

```bash
# Trên Linux/Mac
cp src/main/resources/application.yml.example src/main/resources/application.yml
cp src/main/resources/application-prod.yml.example src/main/resources/application-prod.yml

# Trên Windows
copy src\main\resources\application.yml.example src\main\resources\application.yml
copy src\main\resources\application-prod.yml.example src\main\resources\application-prod.yml
```

2. Các file cấu hình này sẽ sử dụng các biến từ file `.env` của bạn

### Nạp Biến Môi Trường

Có nhiều cách để nạp biến môi trường:

#### Cách 1: Sử dụng env-cmd (Khuyến nghị cho môi trường phát triển)

1. Cài đặt env-cmd:
```bash
npm install -g env-cmd
```

2. Chạy ứng dụng với env-cmd:
```bash
# Với Gradle
env-cmd -f .env ./gradlew bootRun

# Với Maven
env-cmd -f .env mvn spring-boot:run
```

#### Cách 2: Export biến trực tiếp trong terminal

```bash
# Trên Linux/Mac
export $(cat .env | xargs)

# Trên Windows PowerShell
Get-Content .env | ForEach-Object { $_.Trim() } | Where-Object { $_ -ne "" -and $_ -notmatch "^\s*#" } | ForEach-Object { $var = $_.Split('=', 2); Set-Item -Path "Env:$($var[0])" -Value "$($var[1])" }
```

#### Cách 3: Sử dụng plugin Spring Boot

Thêm plugin như `spring-dotenv` vào dự án để tự động nạp file `.env`.

### Lưu ý Quan Trọng

- File `.env` được loại trừ khỏi kiểm soát phiên bản thông qua `.gitignore` để ngăn thông tin nhạy cảm bị tải lên GitHub
- Không bao giờ commit file `.env` thực với thông tin xác thực thực tế vào repository
- Các file cấu hình application.yml và application-prod.yml cũng đã được thêm vào .gitignore
- Nếu bạn thêm biến môi trường mới, hãy cập nhật cả file `.env.example` và các file .example tương ứng

## Chạy Ứng Dụng

Đảm bảo bạn đã thiết lập môi trường đúng cách trước khi chạy ứng dụng:

```bash
# Sử dụng Gradle
./gradlew bootRun

# Sử dụng Maven
mvn spring-boot:run
```

## Triển khai Production

Đối với triển khai production, bạn nên đặt biến môi trường trực tiếp trên nền tảng hosting (Railway, Heroku, v.v.) thay vì sử dụng file `.env`. Điều này an toàn hơn và tuân theo các thực hành tốt nhất cho môi trường production. 