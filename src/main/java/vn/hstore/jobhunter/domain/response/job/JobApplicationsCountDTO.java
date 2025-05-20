package vn.hstore.jobhunter.domain.response.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationsCountDTO {
    private Long jobId;
    private String jobName;
    private Long totalApplications; // Tổng số ứng viên
    private Long cvCount; // Số lượng CV
    private Long resumeCount; // Số lượng resume
} 