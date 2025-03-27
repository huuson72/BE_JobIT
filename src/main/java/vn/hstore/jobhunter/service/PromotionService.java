package vn.hstore.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hstore.jobhunter.domain.Promotion;
import vn.hstore.jobhunter.domain.SubscriptionPackage;
import vn.hstore.jobhunter.repository.PromotionRepository;
import vn.hstore.jobhunter.repository.SubscriptionPackageRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private SubscriptionPackageRepository subscriptionPackageRepository;

    public BigDecimal calculateDiscountedPrice(Long packageId, String promotionCode) {
        SubscriptionPackage subscriptionPackage = subscriptionPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói VIP"));

        if (promotionCode == null || promotionCode.isEmpty()) {
            return subscriptionPackage.getPrice();
        }

        LocalDateTime now = LocalDateTime.now();
        Promotion promotion = promotionRepository.findByCodeAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(
                promotionCode, now, now)
                .orElseThrow(() -> new RuntimeException("Mã ưu đãi không hợp lệ hoặc đã hết hạn"));

        if (!promotion.getSubscriptionPackage().getId().equals(packageId)) {
            throw new RuntimeException("Mã ưu đãi không áp dụng cho gói VIP này");
        }

        BigDecimal originalPrice = subscriptionPackage.getPrice();
        BigDecimal discountPercentage = BigDecimal.valueOf(promotion.getDiscountPercentage());
        BigDecimal discountAmount = originalPrice.multiply(discountPercentage).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
        return originalPrice.subtract(discountAmount);
    }

    public List<Promotion> getActivePromotionsForPackage(Long packageId) {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findBySubscriptionPackageIdAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(
                packageId, now, now);
    }

    public boolean isValidPromotion(String code, Long packageId) {
        try {
            LocalDateTime now = LocalDateTime.now();
            return promotionRepository.findByCodeAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(
                    code, now, now)
                    .map(promotion -> promotion.getSubscriptionPackage().getId().equals(packageId))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }
} 