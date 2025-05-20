package vn.hstore.jobhunter.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Company;
import vn.hstore.jobhunter.domain.Job;
import vn.hstore.jobhunter.domain.Skill;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>,
        JpaSpecificationExecutor<Job> {

    List<Job> findBySkillsIn(List<Skill> skills);

    List<Job> findByCompany(Company company);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.isDeleted = false")
    long countTotalJobs();
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.active = true AND j.isDeleted = false")
    long countActiveJobs();
    
    @Query("SELECT j.level as level, COUNT(j) as count FROM Job j WHERE j.isDeleted = false GROUP BY j.level")
    List<Map<String, Object>> countJobsByLevel();
    
    @Query("SELECT j.location as location, COUNT(j) as count FROM Job j WHERE j.isDeleted = false GROUP BY j.location")
    List<Map<String, Object>> countJobsByLocation();
    
    @Query("SELECT AVG(j.salary) FROM Job j WHERE j.isDeleted = false")
    double getAverageSalary();
    
    @Query("SELECT c.name as companyName, COUNT(j) as jobCount FROM Job j JOIN j.company c WHERE j.isDeleted = false GROUP BY c.name ORDER BY jobCount DESC")
    List<Map<String, Object>> countJobsByCompany();

    // @Query("SELECT j.industry, j.title as jobTitle, COUNT(j) as jobCount, " +
    //        "AVG(j.salary) as averageSalary, j.location, GROUP_CONCAT(j.skills) as skills " +
    //        "FROM Job j GROUP BY j.industry, j.title, j.location")
    // List<Map<String, Object>> getJobStatistics();
}
