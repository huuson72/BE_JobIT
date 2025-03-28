package vn.hstore.jobhunter.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Company;
import vn.hstore.jobhunter.domain.EmployerSubscription;
import vn.hstore.jobhunter.domain.User;

@Repository
public interface EmployerSubscriptionRepository extends JpaRepository<EmployerSubscription, Long>,
        JpaSpecificationExecutor<EmployerSubscription> {
    
    List<EmployerSubscription> findByUserAndStatus(User user, String status);
    
    List<EmployerSubscription> findByCompanyAndStatus(Company company, String status);
    
    @Query("SELECT es FROM EmployerSubscription es WHERE es.user.id = :userId AND es.status = 'ACTIVE' " +
           "AND es.endDate >= :currentDate ORDER BY es.endDate DESC")
    List<EmployerSubscription> findActiveSubscriptionsByUserId(@Param("userId") Long userId, 
                                                              @Param("currentDate") Instant currentDate);
    
    @Query("SELECT es FROM EmployerSubscription es WHERE es.company.id = :companyId AND es.status = 'ACTIVE' " +
           "AND es.endDate >= :currentDate ORDER BY es.endDate DESC")
    List<EmployerSubscription> findActiveSubscriptionsByCompanyId(@Param("companyId") Long companyId, 
                                                                 @Param("currentDate") Instant currentDate);
    
    @Query("SELECT COALESCE(SUM(es.remainingPosts), 0) FROM EmployerSubscription es " +
           "WHERE es.user.id = :userId AND es.status = 'ACTIVE' AND es.endDate >= :currentDate")
    Integer getTotalRemainingPostsByUserId(@Param("userId") Long userId, 
                                          @Param("currentDate") Instant currentDate);
    
    Optional<EmployerSubscription> findByUserAndCompanyAndStatus(User user, Company company, String status);

    List<EmployerSubscription> findByUserIdAndStatusAndEndDateAfter(Long userId, String status, Instant endDate);
} 