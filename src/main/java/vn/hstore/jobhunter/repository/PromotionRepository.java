package vn.hstore.jobhunter.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findBySubscriptionPackageId(Long packageId);
    List<Promotion> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(LocalDateTime startDate, LocalDateTime endDate);
    List<Promotion> findBySubscriptionPackageIdAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(Long packageId, LocalDateTime startDate, LocalDateTime endDate);
    Optional<Promotion> findByCodeAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(String code, LocalDateTime startDate, LocalDateTime endDate);
} 