package vn.hstore.jobhunter.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.hstore.jobhunter.domain.Subscription;
import vn.hstore.jobhunter.domain.Transaction;
import vn.hstore.jobhunter.dto.PaymentRequestDTO;
import vn.hstore.jobhunter.repository.SubscriptionRepository;
import vn.hstore.jobhunter.repository.TransactionRepository;
import vn.hstore.jobhunter.service.VNPayService;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        try {
            String paymentUrl = vnPayService.createPaymentUrl(paymentRequest);
            return ResponseEntity.ok().body(paymentUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating payment: " + e.getMessage());
        }
    }

    // @GetMapping("/vnpay-callback")
    // public ResponseEntity<?> vnPayCallback(@RequestParam Map<String, String> queryParams) {
    //     try {
    //         boolean isValid = vnPayService.validateResponse(queryParams);
    //         if (isValid) {
    //             vnPayService.handlePaymentCallback(queryParams);
    //             String responseCode = queryParams.get("vnp_ResponseCode");
    //             if ("00".equals(responseCode)) {
    //                 // Payment successful
    //                 return ResponseEntity.ok().body("Payment successful");
    //             } else {
    //                 // Payment failed
    //                 return ResponseEntity.badRequest().body("Payment failed");
    //             }
    //         } else {
    //             return ResponseEntity.badRequest().body("Invalid signature");
    //         }
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body("Error processing callback: " + e.getMessage());
    //     }
    // }
    @GetMapping("/vnpay-callback")
    public void vnPayCallback(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam Map<String, String> queryParams
    ) throws IOException {
        try {
            // Log tất cả các tham số để debug
            System.out.println("===== VNPay Callback Parameters: =====");
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            
            boolean isValid = vnPayService.validateResponse(queryParams);
            System.out.println("Signature validation result: " + (isValid ? "VALID" : "INVALID"));
            
            if (isValid) {
                vnPayService.handlePaymentCallback(queryParams);
                System.out.println("Payment callback processed successfully");
            } else {
                System.err.println("Invalid VNPay signature!");
            }

            // Xác định URL của frontend dựa trên môi trường
            String frontendUrl;
            String referer = request.getHeader("Referer");
            System.out.println("Referer: " + referer);
            
            // Nếu request đến từ localhost, sử dụng URL local
            if (referer != null && referer.contains("localhost")) {
                frontendUrl = "http://localhost:3000/subscription/payment-result";
            } else {
                // Sử dụng URL production
                frontendUrl = "https://fe-jobit.onrender.com/subscription/payment-result";
            }
            System.out.println("Using frontend URL: " + frontendUrl);
            
            StringBuilder redirectUrl = new StringBuilder(frontendUrl);
            redirectUrl.append("?");

            // Đảm bảo chuyển tất cả tham số sang frontend
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                redirectUrl.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                        .append("&");
            }

            // Log URL redirect để debug
            System.out.println("Redirecting to: " + redirectUrl.toString());

            // Chuyển hướng
            response.sendRedirect(redirectUrl.toString());
        } catch (Exception e) {
            System.err.println("Error in VNPay callback: " + e.getMessage());
            e.printStackTrace();
            
            // Redirect về frontend với thông báo lỗi
            String errorUrl = "https://fe-jobit.onrender.com/subscription/payment-result?vnp_ResponseCode=99&error=" 
                + URLEncoder.encode("Lỗi xử lý thanh toán: " + e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(errorUrl);
        }
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<?> getTransactionStatus(@PathVariable String orderId) {
        try {
            List<Transaction> transactions = transactionRepository.findByOrderId(orderId);
            if (!transactions.isEmpty()) {
                return ResponseEntity.ok().body(Map.of(
                        "data", transactions.get(0),
                        "success", true
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Không tìm thấy giao dịch với mã " + orderId,
                        "success", false
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Lỗi khi kiểm tra giao dịch: " + e.getMessage(),
                    "success", false
            ));
        }
    }

    @GetMapping("/user/{userId}/subscriptions")
    public ResponseEntity<?> getUserSubscriptions(@PathVariable Long userId) {
        try {
            List<Subscription> subscriptions = subscriptionRepository.findByUserIdAndStatusOrderByStartDateDesc(userId, "ACTIVE");

            if (subscriptions != null && !subscriptions.isEmpty()) {
                return ResponseEntity.ok().body(Map.of(
                        "data", subscriptions,
                        "success", true
                ));
            } else {
                return ResponseEntity.ok().body(Map.of(
                        "message", "Người dùng chưa mua gói VIP nào hoặc không có gói VIP đang hoạt động",
                        "success", false,
                        "data", Collections.emptyList()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Lỗi khi lấy danh sách gói VIP: " + e.getMessage(),
                    "success", false
            ));
        }
    }

}
