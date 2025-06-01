# JobIT - Backend System

## Giới thiệu

Đây là hệ thống backend cho ứng dụng tìm kiếm việc làm JobIT, được xây dựng bằng Spring Boot. Hệ thống cung cấp các API để quản lý người dùng, công ty, tin tuyển dụng, đơn ứng tuyển, gói đăng ký VIP, thanh toán và các chức năng liên quan khác.

## Tính năng chính

Dựa trên codebase hiện tại, các tính năng chính của hệ thống bao gồm:

- **Quản lý người dùng**: Đăng ký, đăng nhập, quản lý hồ sơ người dùng (ứng viên, nhà tuyển dụng, admin).
- **Xác thực và Phân quyền**: Sử dụng JWT và Spring Security để bảo vệ các API endpoint. Hỗ trợ đăng nhập bằng Google (OAuth2).
- **Quản lý Công ty**: Tạo, cập nhật, xóa và xem thông tin công ty.
- **Quản lý Tin tuyển dụng**: Đăng, sửa, xóa, tìm kiếm và xem chi tiết tin tuyển dụng.
- **Quản lý Đơn ứng tuyển**: Ứng viên nộp đơn, nhà tuyển dụng quản lý đơn ứng tuyển.
- **Quản lý Kỹ năng và Địa điểm**: Quản lý danh sách các kỹ năng và địa điểm liên quan đến công việc.
- **Quản lý Gói đăng ký VIP**: Định nghĩa, quản lý các gói đăng ký VIP cho nhà tuyển dụng.
- **Quản lý Đăng ký Nhà tuyển dụng**: Theo dõi các gói VIP đã mua, số lượng bài đăng còn lại và thời hạn sử dụng.
- **Thanh toán**: Tích hợp cổng thanh toán VNPay để xử lý các giao dịch mua gói VIP.
- **Quản lý Khuyến mãi**: Tạo và áp dụng các mã khuyến mãi cho gói đăng ký.
- **Hệ thống Email**: Gửi email thông báo (ví dụ: xác minh tài khoản, kết quả thanh toán).
- **Caching**: Sử dụng Redis để cải thiện hiệu suất đọc dữ liệu.
- **API Documentation**: Sử dụng Swagger/OpenAPI để tự động tạo tài liệu API.
- **Background Jobs**: Sử dụng Spring Scheduler cho các tác vụ định kỳ (ví dụ: dọn dẹp job hết hạn).

## Công nghệ sử dụng (Backend)

- **Framework**: Spring Boot 3.x
- **Ngôn ngữ**: Java 17
- **Security**: Spring Security, JWT, OAuth2
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA, Hibernate
- **Caching**: Redis
- **API**: RESTful API, Spring Web
- **Documentation**: Swagger/OpenAPI
- **Payment Gateway**: VNPay
- **Build Tool**: Gradle (hoặc Maven)
- **Logging**: SLF4J, Logback
- **Testing**: JUnit 5, Mockito
- **Utilities**: Lombok, MapStruct
- **Database Migration**: Flyway (Dựa trên cấu trúc code)

## Yêu cầu hệ thống

- Java Development Kit (JDK) 17 trở lên
- Gradle hoặc Maven (tùy thuộc vào project setup)
- PostgreSQL Database Server
- Redis Server
- Tài khoản Google Cloud Console (để cấu hình Google OAuth2)
- Tài khoản VNPay Merchant (để cấu hình thanh toán)

## Hướng dẫn cài đặt

1.  **Clone Repository**:
    ```bash
    git clone <URL_REPOSITORY>
    cd jobit-backend
    ```

2.  **Thiết lập Database**:
    - Tạo một database PostgreSQL mới.
    - Hệ thống sử dụng Flyway để quản lý database schema. Flyway sẽ tự động chạy các script migration khi ứng dụng khởi động (cần cấu hình kết nối database đúng).

3.  **Thiết lập Redis**:
    - Cài đặt và chạy Redis server.

4.  **Cấu hình Môi trường (.env hoặc application.yml)**:
    - Tạo file `.env` hoặc chỉnh sửa `src/main/resources/application.yml` với các thông tin cấu hình cần thiết. Ví dụ:

    ```yaml
    # Database
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/jobit
        username: your_db_username
        password: your_db_password

    # JWT
    application:
      security:
        jwt:
          secret-key: your_jwt_secret_key_long_enough
          access-token-expiration-ms: 3600000 # 1 hour
          refresh-token-expiration-ms: 86400000 # 24 hours

    # Google OAuth2
    spring:
      security:
        oauth2:
          client:
            registration:
              google:
                client-id: your_google_client_id
                client-secret: your_google_client_secret
                scope:
                  - email
                  - profile

    # VNPay
    vnpay:
      tmnCode: your_vnpay_tmnCode
      hashSecret: your_vnpay_hashSecret
      url: https://sandbox.vnpayment.vn/paymentv2/vpcpay.html # Hoặc URL production
      returnUrl: http://localhost:8080/api/v1/payments/vnpay-callback # Đảm bảo khớp với cấu hình VNPay
      frontendUrl: http://localhost:3000 # URL frontend để redirect

    # Redis
    spring:
      data:
        redis:
          host: localhost
          port: 6379

    # Email
    spring:
      mail:
        host: smtp.gmail.com # or your mail server
        port: 587
        username: your_email@gmail.com
        password: your_email_password # or app password
        properties:
          mail:
            smtp:
              auth: true
              starttls:
                enable: true
    ```
    *(Lưu ý: Không nên lưu secrets trực tiếp trong `application.yml` cho môi trường production. Nên sử dụng environment variables.)*

5.  **Build Dự án**:
    - Sử dụng Gradle:
      ```bash
      ./gradlew clean build
      ```
    - Hoặc Maven:
      ```bash
      mvn clean install
      ```

6.  **Chạy Ứng dụng**:
    - Sau khi build thành công, bạn có thể chạy file JAR:
      ```bash
      java -jar build/libs/jobit-backend.jar # Với Gradle
      # hoặc
      java -jar target/jobit-backend.jar # Với Maven
      ```
    - Hoặc chạy trực tiếp từ IDE.

## API Documentation (Swagger)

Khi ứng dụng đang chạy, bạn có thể truy cập tài liệu API tại địa chỉ:

```
http://localhost:8080/swagger-ui.html
```

(Đảm bảo cổng 8080 không bị chiếm dụng hoặc thay đổi cổng nếu cần trong file cấu hình).

## Cấu trúc Project (High-Level)

```
jobit-backend/
├── src/
│   ├── main/
│   │   ├── java/vn/hstore/jobhunter/
│   │   │   ├── config/          # Cấu hình ứng dụng (Security, Redis, etc.)
│   │   │   ├── controller/      # Các REST Controllers
│   │   │   ├── domain/          # Các lớp Entity (JPA)
│   │   │   ├── repository/      # Các Spring Data JPA Repositories
│   │   │   ├── service/         # Lớp xử lý logic nghiệp vụ
│   │   │   ├── util/            # Các lớp tiện ích (JWT, etc.)
│   │   │   └── ...
│   │   └── resources/
│   │       ├── application.yml  # Cấu hình ứng dụng
│   │       ├── db/migration/    # Flyway migration scripts
│   │       └── ...
│   └── test/              # Code cho Unit/Integration tests
├── build.gradle.kts       # Hoặc pom.xml (Maven)
├── README.md
└── ...
```

## Đóng góp

(Phần này dành cho hướng dẫn đóng góp nếu dự án là mã nguồn mở. Bạn có thể thêm các mục như báo cáo lỗi, yêu cầu tính năng, quy trình pull request.)

## Giấy phép

(Thông tin về giấy phép của dự án, ví dụ: MIT, Apache 2.0, v.v.) 
