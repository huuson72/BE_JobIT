package vn.hstore.jobhunter.service;

import org.springframework.stereotype.Service;

import vn.hstore.jobhunter.domain.DashboardStatsDTO;
import vn.hstore.jobhunter.repository.CompanyRepository;
import vn.hstore.jobhunter.repository.JobRepository;
import vn.hstore.jobhunter.repository.SubscriberRepository;

@Service
public class DashboardService {

    private final CompanyRepository companyRepo;
    private final JobRepository jobRepo;
    private final SubscriberRepository applicantRepo;

    public DashboardService(CompanyRepository companyRepo, JobRepository jobRepo,
            SubscriberRepository applicantRepo) {
        this.companyRepo = companyRepo;
        this.jobRepo = jobRepo;
        this.applicantRepo = applicantRepo;

    }

    public DashboardStatsDTO getDashboardStats() {
        long totalCompanies = companyRepo.count();
        long totalJobs = jobRepo.count();
        long totalApplicants = applicantRepo.count();

        return new DashboardStatsDTO(totalCompanies, totalJobs, totalApplicants);
    }
}
