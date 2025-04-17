package vn.hstore.jobhunter.service;

import vn.hstore.jobhunter.config.VNPayConfig;
import vn.hstore.jobhunter.domain.Transaction;
import vn.hstore.jobhunter.domain.User;
import vn.hstore.jobhunter.domain.Company;
import vn.hstore.jobhunter.domain.Subscription;
import vn.hstore.jobhunter.domain.SubscriptionPackage;
import vn.hstore.jobhunter.dto.PaymentRequestDTO;
import vn.hstore.jobhunter.repository.TransactionRepository;
import vn.hstore.jobhunter.repository.UserRepository;
import vn.hstore.jobhunter.repository.CompanyRepository;
import vn.hstore.jobhunter.repository.SubscriptionPackageRepository;
import vn.hstore.jobhunter.repository.SubscriptionRepository;
import vn.hstore.jobhunter.util.constant.VerificationStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VNPayService {

    @Autowired
    private VNPayConfig vnPayConfig;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SubscriptionPackageRepository subscriptionPackageRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public String createPaymentUrl(PaymentRequestDTO paymentRequest) {
        // Kiểm tra user tồn tại
        User user = userRepository.findById(paymentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra trạng thái xác minh (bỏ qua nếu là admin)
        if (user.getVerificationStatus() == null || user.getVerificationStatus() != VerificationStatus.VERIFIED) {
            // Kiểm tra nếu là admin thì cho phép
            if (user.getRole() == null || 
                (!user.getRole().getName().equals("SUPER_ADMIN") && 
                 !user.getRole().getName().equals("ADMIN"))) {
                throw new RuntimeException("Account not verified. Please wait for admin approval.");
            }
        }

        // Kiểm tra công ty tồn tại
        Company company = companyRepository.findById(paymentRequest.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Kiểm tra gói VIP tồn tại
        SubscriptionPackage subscriptionPackage = subscriptionPackageRepository.findById(paymentRequest.getPackageId())
                .orElseThrow(() -> new RuntimeException("Subscription package not found"));

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = generateTxnRef();
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = vnPayConfig.getTmnCode();
        String vnp_HashSecret = vnPayConfig.getHashSecret();
        String vnp_Url = vnPayConfig.getUrl();
        String vnp_ReturnUrl = vnPayConfig.getReturnUrl();

        // Lưu thông tin giao dịch
        Transaction transaction = new Transaction();
        transaction.setOrderId(vnp_TxnRef);
        transaction.setAmount(paymentRequest.getAmount());
        transaction.setOrderInfo(paymentRequest.getOrderInfo());
        transaction.setStatus("PENDING");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setUser(user);
        transaction.setCompany(company);
        transaction.setSubscriptionPackage(subscriptionPackage);
        transactionRepository.save(transaction);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(paymentRequest.getAmount() * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", paymentRequest.getOrderInfo());
        vnp_Params.put("vnp_OrderType", paymentRequest.getOrderType());
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Đặt múi giờ rõ ràng là GMT+7 (Việt Nam)
        TimeZone vnTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar cld = Calendar.getInstance(vnTimeZone);
        
        // Lấy thời gian hiện tại theo múi giờ GMT+7
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(vnTimeZone);
        
        // In ra log để debug
        System.out.println("Current VN time: " + formatter.format(cld.getTime()));
        
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        
        // Tăng thời gian hết hạn lên 180 phút (3 giờ)
        cld.add(Calendar.MINUTE, 180);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        System.out.println("Expire VN time: " + vnp_ExpireDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnp_Url + "?" + queryUrl;

        return paymentUrl;
    }

    public void handlePaymentCallback(Map<String, String> responseParams) {
        String orderId = responseParams.get("vnp_TxnRef");
        List<Transaction> transactions = transactionRepository.findByOrderId(orderId);
        Transaction transaction = transactions.isEmpty() ? null : transactions.get(0);
        
        if (transaction != null) {
            transaction.setTransactionNo(responseParams.get("vnp_TransactionNo"));
            transaction.setResponseCode(responseParams.get("vnp_ResponseCode"));
            transaction.setPaymentDate(responseParams.get("vnp_PayDate"));
            transaction.setUpdatedAt(LocalDateTime.now());

            if ("00".equals(responseParams.get("vnp_ResponseCode"))) {
                transaction.setStatus("SUCCESS");
                // Kích hoạt gói VIP
                activateSubscription(transaction);
            } else {
                transaction.setStatus("FAILED");
            }

            transactionRepository.save(transaction);
        }
    }

    private void activateSubscription(Transaction transaction) {
        // Tạo subscription mới
        Subscription subscription = new Subscription();
        subscription.setUser(transaction.getUser());
        subscription.setCompany(transaction.getCompany());
        subscription.setSubscriptionPackage(transaction.getSubscriptionPackage());
        subscription.setRemainingPosts(transaction.getSubscriptionPackage().getJobPostLimit());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusMonths(1));
        subscription.setStatus("ACTIVE");
        subscriptionRepository.save(subscription);
    }

    private String generateTxnRef() {
        // Kết hợp timestamp với UUID để đảm bảo mã giao dịch luôn duy nhất
        return System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac sha512_HMAC = javax.crypto.Mac.getInstance("HmacSHA512");
            sha512_HMAC.init(new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA512"));
            byte[] hash = sha512_HMAC.doFinal(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC", e);
        }
    }

    public boolean validateResponse(Map<String, String> responseParams) {
        String vnp_SecureHash = responseParams.get("vnp_SecureHash");
        responseParams.remove("vnp_SecureHash");
        responseParams.remove("vnp_SecureHashType");
        
        List fieldNames = new ArrayList(responseParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) responseParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }
        
        String calculatedHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        return calculatedHash.equals(vnp_SecureHash);
    }
} 