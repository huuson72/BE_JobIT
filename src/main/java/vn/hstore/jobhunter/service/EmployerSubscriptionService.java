package vn.hstore.jobhunter.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.hstore.jobhunter.domain.Company;
import vn.hstore.jobhunter.domain.EmployerSubscription;
import vn.hstore.jobhunter.domain.JobPostingUsage;
import vn.hstore.jobhunter.domain.SubscriptionPackage;
import vn.hstore.jobhunter.domain.User;
import vn.hstore.jobhunter.repository.CompanyRepository;
import vn.hstore.jobhunter.repository.EmployerSubscriptionRepository;
import vn.hstore.jobhunter.repository.JobPostingUsageRepository;
import vn.hstore.jobhunter.repository.SubscriptionPackageRepository;
import vn.hstore.jobhunter.repository.UserRepository;

@Service
public class EmployerSubscriptionService {

    private final EmployerSubscriptionRepository employerSubscriptionRepository;
    private final JobPostingUsageRepository jobPostingUsageRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final SubscriptionPackageRepository subscriptionPackageRepository;

    public EmployerSubscriptionService(
            EmployerSubscriptionRepository employerSubscriptionRepository,
            JobPostingUsageRepository jobPostingUsageRepository,
            UserRepository userRepository,
            CompanyRepository companyRepository,
            SubscriptionPackageRepository subscriptionPackageRepository) {
        this.employerSubscriptionRepository = employerSubscriptionRepository;
        this.jobPostingUsageRepository = jobPostingUsageRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.subscriptionPackageRepository = subscriptionPackageRepository;
    }
    
    @Transactional
    public EmployerSubscription purchaseSubscription(Long userId, Long companyId, Long packageId, String paymentMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công ty"));
        
        SubscriptionPackage subscriptionPackage = subscriptionPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói đăng ký"));
        
        Instant now = Instant.now();
        Instant endDate = now.plus(subscriptionPackage.getDurationDays(), ChronoUnit.DAYS);
        
        EmployerSubscription subscription = new EmployerSubscription();
        subscription.setUser(user);
        subscription.setCompany(company);
        subscription.setSubscriptionPackage(subscriptionPackage);
        subscription.setStartDate(now);
        subscription.setEndDate(endDate);
        subscription.setRemainingPosts(subscriptionPackage.getJobPostLimit());
        subscription.setIsActive(true);
        subscription.setPaymentMethod(paymentMethod);
        subscription.setTransactionId("TXN-" + now.toEpochMilli()); // Giả lập mã giao dịch
        
        return employerSubscriptionRepository.save(subscription);
    }
    
    /**
     * Kiểm tra và cập nhật số lượt đăng tin còn lại khi người dùng đăng tin mới
     */
    @Transactional
    public Map<String, Object> checkAndUpdateJobPostingQuota(Long userId, Long companyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công ty"));
        
        Map<String, Object> result = new HashMap<>();
        result.put("canPost", false);
        result.put("message", "");
        
        // Kiểm tra quota miễn phí hàng ngày
        LocalDate today = LocalDate.now();
        Optional<JobPostingUsage> usageOpt = jobPostingUsageRepository.findByUserAndCompanyAndPostingDate(
                user, company, today);
        
        JobPostingUsage usage;
        if (usageOpt.isPresent()) {
            usage = usageOpt.get();
            // Nếu chưa dùng hết quota miễn phí
            if (usage.getUsedCount() < usage.getFreeLimit()) {
                usage.setUsedCount(usage.getUsedCount() + 1);
                jobPostingUsageRepository.save(usage);
                
                result.put("canPost", true);
                result.put("message", "Sử dụng lượt đăng miễn phí. Còn lại " + 
                            (usage.getFreeLimit() - usage.getUsedCount()) + "/" + usage.getFreeLimit() + " lượt miễn phí hôm nay.");
                return result;
            }
        } else {
            // Tạo bản ghi mới theo dõi sử dụng hằng ngày
            usage = new JobPostingUsage();
            usage.setUser(user);
            usage.setCompany(company);
            usage.setPostingDate(today);
            usage.setUsedCount(1); // Đã sử dụng 1 lượt
            jobPostingUsageRepository.save(usage);
            
            result.put("canPost", true);
            result.put("message", "Sử dụng lượt đăng miễn phí. Còn lại " + 
                        (usage.getFreeLimit() - usage.getUsedCount()) + "/" + usage.getFreeLimit() + " lượt miễn phí hôm nay.");
            return result;
        }
        
        // Nếu đã hết quota miễn phí, kiểm tra gói VIP
        Instant now = Instant.now();
        List<EmployerSubscription> activeSubscriptions = employerSubscriptionRepository
                .findActiveSubscriptionsByUserId(userId, now);
        
        if (activeSubscriptions.isEmpty()) {
            result.put("message", "Bạn đã hết lượt đăng miễn phí hôm nay. Vui lòng mua gói VIP để đăng thêm.");
            return result;
        }
        
        // Ưu tiên sử dụng gói sắp hết hạn trước
        for (EmployerSubscription subscription : activeSubscriptions) {
            if (subscription.getRemainingPosts() > 0) {
                subscription.setRemainingPosts(subscription.getRemainingPosts() - 1);
                employerSubscriptionRepository.save(subscription);
                
                result.put("canPost", true);
                result.put("message", "Sử dụng lượt đăng từ gói " + subscription.getSubscriptionPackage().getName() + 
                            ". Còn lại " + subscription.getRemainingPosts() + " lượt.");
                return result;
            }
        }
        
        result.put("message", "Bạn đã hết lượt đăng tin từ tất cả các gói đăng ký. Vui lòng mua thêm gói VIP.");
        return result;
    }
    
    public Integer getTotalRemainingPostsByUserId(Long userId) {
        return employerSubscriptionRepository.getTotalRemainingPostsByUserId(userId, Instant.now());
    }
    
    public List<EmployerSubscription> getActiveSubscriptionsByUserId(Long userId) {
        return employerSubscriptionRepository.findActiveSubscriptionsByUserId(userId, Instant.now());
    }
    
    public Map<String, Object> getEmployerSubscriptionStatus(Long userId, Long companyId) {
        Map<String, Object> status = new HashMap<>();
        
        // Kiểm tra quota miễn phí hôm nay
        LocalDate today = LocalDate.now();
        Optional<JobPostingUsage> usageOpt = jobPostingUsageRepository.findByUserAndCompanyAndPostingDate(
                userRepository.findById(userId).orElse(null), 
                companyRepository.findById(companyId).orElse(null), 
                today);
        
        int usedFreeToday = 0;
        int freeLimit = 3;
        
        if (usageOpt.isPresent()) {
            JobPostingUsage usage = usageOpt.get();
            usedFreeToday = usage.getUsedCount();
            freeLimit = usage.getFreeLimit();
        }
        
        status.put("freeQuotaToday", freeLimit);
        status.put("usedFreeToday", usedFreeToday);
        status.put("remainingFreeToday", freeLimit - usedFreeToday);
        
        // Thông tin gói VIP
        List<EmployerSubscription> activeSubscriptions = employerSubscriptionRepository
                .findActiveSubscriptionsByUserId(userId, Instant.now());
        
        int totalVipRemaining = getTotalRemainingPostsByUserId(userId);
        
        status.put("hasVipPackage", !activeSubscriptions.isEmpty());
        status.put("totalVipRemaining", totalVipRemaining);
        status.put("activeSubscriptions", activeSubscriptions);
        
        return status;
    }
} 