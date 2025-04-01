package vn.hstore.jobhunter.domain.response.admin;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class RevenueStatisticsDTO {
    private Long totalRevenue;
    private Long todayRevenue;
    private Long lastWeekRevenue;
    private Long lastMonthRevenue;
    
    private List<Map<String, Object>> revenueByPackage;
    private List<Map<String, Object>> revenueByCompany;
    private List<Map<String, Object>> revenueByMonth;
    private List<Map<String, Object>> transactionCountByStatus;
    private List<Map<String, Object>> dailyRevenueLastWeek;
    
    // Tính toán các chỉ số tăng trưởng
    private Double growthRateLastWeek; // Tỉ lệ tăng trưởng so với tuần trước (%)
    private Double growthRateLastMonth; // Tỉ lệ tăng trưởng so với tháng trước (%)
    
    // Các thông tin tổng quan
    private Long totalTransactions;
    private Long successfulTransactions;
    private Long failedTransactions;
    private Double successRate; // Tỉ lệ giao dịch thành công (%)
} 