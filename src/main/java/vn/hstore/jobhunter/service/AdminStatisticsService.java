package vn.hstore.jobhunter.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import vn.hstore.jobhunter.domain.response.admin.AdminStatisticsDTO;
import vn.hstore.jobhunter.repository.CompanyRepository;
import vn.hstore.jobhunter.repository.CVRepository;
import vn.hstore.jobhunter.repository.JobRepository;
import vn.hstore.jobhunter.repository.UserRepository;

@Service
public class AdminStatisticsService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CVRepository cvRepository;

    public AdminStatisticsService(
            JobRepository jobRepository,
            UserRepository userRepository,
            CompanyRepository companyRepository,
            CVRepository cvRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.cvRepository = cvRepository;
    }

    public AdminStatisticsDTO getStatistics() {
        AdminStatisticsDTO statistics = new AdminStatisticsDTO();

        // Job Statistics
        statistics.setTotalJobs(jobRepository.countTotalJobs());
        statistics.setActiveJobs(jobRepository.countActiveJobs());
        statistics.setJobsByLevel(jobRepository.countJobsByLevel());
        statistics.setJobsByLocation(jobRepository.countJobsByLocation());
        statistics.setAverageSalary(jobRepository.getAverageSalary());
        statistics.setJobsByCompany(jobRepository.countJobsByCompany());

        // User Statistics
        statistics.setTotalUsers(userRepository.count());
        statistics.setUsersByRole(userRepository.countUsersByRole());

        // Get new users in last 7 days
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        statistics.setNewUsersByTime(userRepository.countNewUsersByTime(sevenDaysAgo));

        // Company Statistics
        statistics.setTotalCompanies(companyRepository.count());
        statistics.setNewCompaniesByTime(companyRepository.countNewCompaniesByTime(sevenDaysAgo));

        // CV Statistics
        statistics.setTotalCVs(cvRepository.count());
        statistics.setCvsByStatus(cvRepository.countCVsByStatus());
        statistics.setTopSkills(cvRepository.getTopSkills());

        return statistics;
    }

    public AdminStatisticsDTO getJobStatistics() {
        AdminStatisticsDTO statistics = new AdminStatisticsDTO();
        statistics.setTotalJobs(jobRepository.countTotalJobs());
        statistics.setActiveJobs(jobRepository.countActiveJobs());
        statistics.setJobsByLevel(jobRepository.countJobsByLevel());
        statistics.setJobsByLocation(jobRepository.countJobsByLocation());
        statistics.setAverageSalary(jobRepository.getAverageSalary());
        statistics.setJobsByCompany(jobRepository.countJobsByCompany());
        return statistics;
    }

    public AdminStatisticsDTO getUserStatistics() {
        AdminStatisticsDTO statistics = new AdminStatisticsDTO();
        statistics.setTotalUsers(userRepository.count());
        statistics.setUsersByRole(userRepository.countUsersByRole());
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        statistics.setNewUsersByTime(userRepository.countNewUsersByTime(sevenDaysAgo));
        return statistics;
    }

    public AdminStatisticsDTO getCompanyStatistics() {
        AdminStatisticsDTO statistics = new AdminStatisticsDTO();
        statistics.setTotalCompanies(companyRepository.count());
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        statistics.setNewCompaniesByTime(companyRepository.countNewCompaniesByTime(sevenDaysAgo));
        return statistics;
    }

    public AdminStatisticsDTO getCVStatistics() {
        AdminStatisticsDTO statistics = new AdminStatisticsDTO();
        statistics.setTotalCVs(cvRepository.count());
        statistics.setCvsByStatus(cvRepository.countCVsByStatus());
        statistics.setTopSkills(cvRepository.getTopSkills());
        return statistics;
    }

    public AdminStatisticsDTO getRecentStatistics() {
        AdminStatisticsDTO statistics = new AdminStatisticsDTO();
        
        // Lấy thống kê trong 7 ngày gần nhất
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        
        // Thống kê việc làm mới
        statistics.setActiveJobs(jobRepository.countActiveJobs());
        statistics.setJobsByLevel(jobRepository.countJobsByLevel());
        
        // Thống kê người dùng mới
        statistics.setNewUsersByTime(userRepository.countNewUsersByTime(sevenDaysAgo));
        
        // Thống kê công ty mới
        statistics.setNewCompaniesByTime(companyRepository.countNewCompaniesByTime(sevenDaysAgo));
        
        // Thống kê CV mới
        statistics.setCvsByStatus(cvRepository.countCVsByStatus());
        
        return statistics;
    }

    public AdminStatisticsDTO getTrendStatistics() {
        AdminStatisticsDTO statistics = new AdminStatisticsDTO();
        
        // Lấy thống kê trong 30 ngày để phân tích xu hướng
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        
        // Xu hướng việc làm theo địa điểm
        statistics.setJobsByLocation(jobRepository.countJobsByLocation());
        
        // Xu hướng việc làm theo công ty
        statistics.setJobsByCompany(jobRepository.countJobsByCompany());
        
        // Xu hướng mức lương
        statistics.setAverageSalary(jobRepository.getAverageSalary());
        
        // Xu hướng kỹ năng phổ biến
        statistics.setTopSkills(cvRepository.getTopSkills());
        
        return statistics;
    }
} 