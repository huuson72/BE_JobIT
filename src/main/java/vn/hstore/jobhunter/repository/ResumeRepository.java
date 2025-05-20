package vn.hstore.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>,
        JpaSpecificationExecutor<Resume> {

    boolean existsByJobId(Long jobId);
    
    @Query("SELECT COUNT(r) FROM Resume r WHERE r.job.id = :jobId AND r.isDeleted = false")
    long countByJobId(@Param("jobId") Long jobId);

}
