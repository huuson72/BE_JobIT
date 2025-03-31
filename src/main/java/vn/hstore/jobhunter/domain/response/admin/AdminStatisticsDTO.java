package vn.hstore.jobhunter.domain.response.admin;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminStatisticsDTO {
    // Job Statistics
    private long totalJobs;
    private long activeJobs;
    private List<Map<String, Object>> jobsByLevel;
    private List<Map<String, Object>> jobsByLocation;
    private double averageSalary;
    private List<Map<String, Object>> jobsByCompany;

    // User Statistics
    private long totalUsers;
    private List<Map<String, Object>> usersByRole;
    private List<Map<String, Object>> newUsersByTime;

    // Company Statistics
    private long totalCompanies;
    private List<Map<String, Object>> newCompaniesByTime;

    // CV/Resume Statistics
    private long totalCVs;
    private List<Map<String, Object>> cvsByStatus;
    private List<Map<String, Object>> topSkills;
} 