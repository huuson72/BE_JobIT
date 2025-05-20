package vn.hstore.jobhunter.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findBySubscriptionPackageIdAndIsDeletedFalse(Long packageId);
    
    List<Promotion> findByCodeAndIsDeletedFalse(String code);
    
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= :now AND p.endDate >= :now2 AND p.isDeleted = false")
    List<Promotion> findByIsActiveTrueAndStartDateBeforeAndEndDateAfterAndNotDeleted(
            @Param("now") LocalDateTime now, @Param("now2") LocalDateTime now2);
    
    @Query("SELECT p FROM Promotion p WHERE p.subscriptionPackage.id = :packageId AND p.isActive = true AND p.startDate <= :now AND p.endDate >= :now2 AND p.isDeleted = false")
    List<Promotion> findBySubscriptionPackageIdAndIsActiveTrueAndStartDateBeforeAndEndDateAfterAndNotDeleted(
            @Param("packageId") Long packageId, @Param("now") LocalDateTime now, @Param("now2") LocalDateTime now2);
    
    // Giữ các phương thức cũ để tương thích ngược
    List<Promotion> findBySubscriptionPackageId(Long packageId);
    List<Promotion> findByCode(String code);
    List<Promotion> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(LocalDateTime now, LocalDateTime now2);
    List<Promotion> findBySubscriptionPackageIdAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(
            Long packageId, LocalDateTime now, LocalDateTime now2);
} 