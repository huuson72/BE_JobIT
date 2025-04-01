package vn.hstore.jobhunter.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Tổng doanh thu
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'success'")
    Long getTotalRevenue();
    
    // Tổng doanh thu theo khoảng thời gian
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'success' AND t.createdAt BETWEEN :startDate AND :endDate")
    Long getRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Doanh thu theo gói đăng ký
    @Query("SELECT p.name as packageName, SUM(t.amount) as revenue, COUNT(t) as count " +
           "FROM Transaction t JOIN t.subscriptionPackage p " +
           "WHERE t.status = 'success' " +
           "GROUP BY p.name")
    List<Map<String, Object>> getRevenueByPackage();
    
    // Doanh thu theo công ty
    @Query("SELECT c.name as companyName, SUM(t.amount) as revenue, COUNT(t) as count " +
           "FROM Transaction t JOIN t.company c " +
           "WHERE t.status = 'success' " +
           "GROUP BY c.name " +
           "ORDER BY revenue DESC")
    List<Map<String, Object>> getRevenueByCompany();
    
    // Doanh thu theo tháng trong năm
    @Query("SELECT FUNCTION('MONTH', t.createdAt) as month, SUM(t.amount) as revenue " +
           "FROM Transaction t " +
           "WHERE t.status = 'success' AND FUNCTION('YEAR', t.createdAt) = :year " +
           "GROUP BY FUNCTION('MONTH', t.createdAt) " +
           "ORDER BY month")
    List<Map<String, Object>> getRevenueByMonth(@Param("year") int year);
    
    // Số lượng giao dịch theo trạng thái
    @Query("SELECT t.status as status, COUNT(t) as count " +
           "FROM Transaction t " +
           "GROUP BY t.status")
    List<Map<String, Object>> getTransactionCountByStatus();
    
    // Doanh thu ngày hôm nay
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
           "WHERE t.status = 'success' AND DATE(t.createdAt) = CURRENT_DATE")
    Long getTodayRevenue();
    
    // Doanh thu 7 ngày gần nhất
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
           "WHERE t.status = 'success' AND t.createdAt >= :lastWeek")
    Long getLastWeekRevenue(@Param("lastWeek") LocalDateTime lastWeek);
    
    // Doanh thu theo từng ngày trong 7 ngày gần nhất
    @Query("SELECT DATE(t.createdAt) as date, SUM(t.amount) as revenue " +
           "FROM Transaction t " +
           "WHERE t.status = 'success' AND t.createdAt >= :lastWeek " +
           "GROUP BY DATE(t.createdAt) " +
           "ORDER BY date")
    List<Map<String, Object>> getDailyRevenueLastWeek(@Param("lastWeek") LocalDateTime lastWeek);
} 