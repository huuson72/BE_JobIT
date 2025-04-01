package vn.hstore.jobhunter.service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import vn.hstore.jobhunter.domain.response.admin.RevenueStatisticsDTO;
import vn.hstore.jobhunter.repository.TransactionRepository;

@Service
public class RevenueStatisticsService {

    private final TransactionRepository transactionRepository;

    public RevenueStatisticsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public RevenueStatisticsDTO getRevenueStatistics() {
        RevenueStatisticsDTO statistics = new RevenueStatisticsDTO();
        
        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeek = now.minusDays(7);
        LocalDateTime twoWeeksAgo = now.minusDays(14);
        LocalDateTime lastMonth = now.minusMonths(1);
        LocalDateTime twoMonthsAgo = now.minusMonths(2);
        
        // Tổng doanh thu
        Long totalRevenue = transactionRepository.getTotalRevenue();
        statistics.setTotalRevenue(totalRevenue != null ? totalRevenue : 0L);
        
        // Doanh thu hôm nay
        Long todayRevenue = transactionRepository.getTodayRevenue();
        statistics.setTodayRevenue(todayRevenue != null ? todayRevenue : 0L);
        
        // Doanh thu 7 ngày gần nhất
        Long lastWeekRevenue = transactionRepository.getLastWeekRevenue(lastWeek);
        statistics.setLastWeekRevenue(lastWeekRevenue != null ? lastWeekRevenue : 0L);
        
        // Doanh thu tháng gần nhất
        Long lastMonthRevenue = transactionRepository.getRevenueByDateRange(lastMonth, now);
        statistics.setLastMonthRevenue(lastMonthRevenue != null ? lastMonthRevenue : 0L);
        
        // Tính tỉ lệ tăng trưởng tuần
        Long previousWeekRevenue = transactionRepository.getRevenueByDateRange(twoWeeksAgo, lastWeek);
        if (previousWeekRevenue != null && previousWeekRevenue > 0) {
            double growthRate = ((double) (lastWeekRevenue - previousWeekRevenue) / previousWeekRevenue) * 100;
            statistics.setGrowthRateLastWeek(Math.round(growthRate * 100.0) / 100.0); // Làm tròn 2 chữ số thập phân
        }
        
        // Tính tỉ lệ tăng trưởng tháng
        Long previousMonthRevenue = transactionRepository.getRevenueByDateRange(twoMonthsAgo, lastMonth);
        if (previousMonthRevenue != null && previousMonthRevenue > 0) {
            double growthRate = ((double) (lastMonthRevenue - previousMonthRevenue) / previousMonthRevenue) * 100;
            statistics.setGrowthRateLastMonth(Math.round(growthRate * 100.0) / 100.0); // Làm tròn 2 chữ số thập phân
        }
        
        // Doanh thu theo gói đăng ký
        List<Map<String, Object>> revenueByPackage = transactionRepository.getRevenueByPackage();
        statistics.setRevenueByPackage(revenueByPackage);
        
        // Doanh thu theo công ty
        List<Map<String, Object>> revenueByCompany = transactionRepository.getRevenueByCompany();
        statistics.setRevenueByCompany(revenueByCompany);
        
        // Doanh thu theo tháng trong năm
        List<Map<String, Object>> revenueByMonth = transactionRepository.getRevenueByMonth(Year.now().getValue());
        statistics.setRevenueByMonth(revenueByMonth);
        
        // Số lượng giao dịch theo trạng thái
        List<Map<String, Object>> transactionCountByStatus = transactionRepository.getTransactionCountByStatus();
        statistics.setTransactionCountByStatus(transactionCountByStatus);
        
        // Doanh thu theo từng ngày trong 7 ngày gần nhất
        List<Map<String, Object>> dailyRevenueLastWeek = transactionRepository.getDailyRevenueLastWeek(lastWeek);
        statistics.setDailyRevenueLastWeek(dailyRevenueLastWeek);
        
        // Tính toán các thông tin tổng quan
        Long totalTransactions = 0L;
        Long successfulTransactions = 0L;
        Long failedTransactions = 0L;
        
        for (Map<String, Object> statusCount : transactionCountByStatus) {
            String status = (String) statusCount.get("status");
            Long count = ((Number) statusCount.get("count")).longValue();
            
            totalTransactions += count;
            if ("success".equals(status)) {
                successfulTransactions = count;
            } else if ("failed".equals(status)) {
                failedTransactions = count;
            }
        }
        
        statistics.setTotalTransactions(totalTransactions);
        statistics.setSuccessfulTransactions(successfulTransactions);
        statistics.setFailedTransactions(failedTransactions);
        
        // Tính tỉ lệ thành công
        if (totalTransactions > 0) {
            double successRate = ((double) successfulTransactions / totalTransactions) * 100;
            statistics.setSuccessRate(Math.round(successRate * 100.0) / 100.0); // Làm tròn 2 chữ số thập phân
        }
        
        return statistics;
    }
    
    // Lấy thống kê doanh thu theo khoảng thời gian
    public RevenueStatisticsDTO getRevenueStatisticsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        RevenueStatisticsDTO statistics = new RevenueStatisticsDTO();
        
        // Tổng doanh thu trong khoảng thời gian
        Long revenueInRange = transactionRepository.getRevenueByDateRange(startDate, endDate);
        statistics.setTotalRevenue(revenueInRange != null ? revenueInRange : 0L);
        
        // TODO: Thêm các thống kê khác theo khoảng thời gian
        
        return statistics;
    }
} 