package vn.hstore.jobhunter.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findBySubscriptionPackageId(Long packageId);
    List<Promotion> findByCode(String code);
    List<Promotion> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(LocalDateTime now, LocalDateTime now2);
    List<Promotion> findBySubscriptionPackageIdAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(
            Long packageId, LocalDateTime now, LocalDateTime now2);
} 