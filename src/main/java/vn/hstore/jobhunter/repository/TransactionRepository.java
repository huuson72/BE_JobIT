package vn.hstore.jobhunter.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Transaction;
import vn.hstore.jobhunter.domain.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByOrderId(String orderId);
    List<Transaction> findByStatus(String status);
    List<Transaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> findByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, String status);
    Transaction findFirstByUserOrderByCreatedAtDesc(User user);
} 