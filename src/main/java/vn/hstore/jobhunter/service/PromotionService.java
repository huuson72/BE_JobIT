package vn.hstore.jobhunter.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Data;
import vn.hstore.jobhunter.domain.Promotion;
import vn.hstore.jobhunter.domain.SubscriptionPackage;
import vn.hstore.jobhunter.repository.PromotionRepository;
import vn.hstore.jobhunter.repository.SubscriptionPackageRepository;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private SubscriptionPackageRepository subscriptionPackageRepository;

    @Data
    public static class DiscountResult {
        private BigDecimal originalPrice;
        private BigDecimal finalPrice;
        private Double discountPercentage;
        private String promotionName;
    }

    public DiscountResult calculateDiscountedPrice(Long packageId) {
        // Lấy gói VIP
        SubscriptionPackage subscriptionPackage = subscriptionPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói VIP"));

        DiscountResult result = new DiscountResult();
        result.setOriginalPrice(subscriptionPackage.getPrice());

        // Lấy danh sách ưu đãi đang hoạt động cho gói VIP
        LocalDateTime now = LocalDateTime.now();
        List<Promotion> activePromotions = promotionRepository
                .findBySubscriptionPackageIdAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(
                        packageId, now, now);

        // Nếu không có ưu đãi đang hoạt động, trả về giá gốc
        if (activePromotions.isEmpty()) {
            result.setFinalPrice(subscriptionPackage.getPrice());
            result.setDiscountPercentage(0.0);
            result.setPromotionName(null);
            return result;
        }

        // Tìm ưu đãi có phần trăm giảm giá cao nhất
        Promotion bestPromotion = activePromotions.stream()
                .max((p1, p2) -> Double.compare(p1.getDiscountPercentage(), p2.getDiscountPercentage()))
                .get();

        // Tính giá sau khi giảm giá
        BigDecimal originalPrice = subscriptionPackage.getPrice();
        BigDecimal discountAmount = originalPrice.multiply(BigDecimal.valueOf(bestPromotion.getDiscountPercentage() / 100));
        BigDecimal finalPrice = originalPrice.subtract(discountAmount);

        result.setFinalPrice(finalPrice);
        result.setDiscountPercentage(bestPromotion.getDiscountPercentage());
        result.setPromotionName(bestPromotion.getName());

        return result;
    }
} 