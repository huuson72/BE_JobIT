package vn.hstore.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserIdAndStatusOrderByStartDateDesc(Long userId, String status);
    List<Subscription> findByStatus(String status);
} 