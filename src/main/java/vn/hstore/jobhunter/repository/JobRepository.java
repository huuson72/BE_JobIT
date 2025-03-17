package vn.hstore.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Company;
import vn.hstore.jobhunter.domain.Job;
import vn.hstore.jobhunter.domain.Skill;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>,
        JpaSpecificationExecutor<Job> {

    List<Job> findBySkillsIn(List<Skill> skills);

    List<Job> findByCompany(Company company);

    // @Query("SELECT j.industry, j.title as jobTitle, COUNT(j) as jobCount, " +
    //        "AVG(j.salary) as averageSalary, j.location, GROUP_CONCAT(j.skills) as skills " +
    //        "FROM Job j GROUP BY j.industry, j.title, j.location")
    // List<Map<String, Object>> getJobStatistics();
}
