package vn.hstore.jobhunter.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Company;
import vn.hstore.jobhunter.domain.JobPostingUsage;
import vn.hstore.jobhunter.domain.User;

@Repository
public interface JobPostingUsageRepository extends JpaRepository<JobPostingUsage, Long>,
        JpaSpecificationExecutor<JobPostingUsage> {
    
    Optional<JobPostingUsage> findByUserAndCompanyAndPostingDate(User user, Company company, LocalDate postingDate);
    
} 