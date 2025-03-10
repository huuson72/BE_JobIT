package vn.hstore.jobhunter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {

    private long totalCompanies;  // Tổng số công ty đã đăng ký
    private long totalJobs;       // Tổng số công việc
    private long totalApplicants; // Tổng số ứng viên

}
