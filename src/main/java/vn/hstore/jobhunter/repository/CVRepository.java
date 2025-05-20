package vn.hstore.jobhunter.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.CV;

@Repository
public interface CVRepository extends JpaRepository<CV, Long> {

    List<CV> findByUserId(Long userId);

    @Query("SELECT c.active as status, COUNT(c) as count FROM CV c GROUP BY c.active")
    List<Map<String, Object>> countCVsByStatus();

    @Query("SELECT COUNT(c) FROM CV c WHERE c.job.id = :jobId AND c.isDeleted = false")
    long countByJobId(@Param("jobId") Long jobId);

    @Query(value = "SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(c.skills, '\n', n.n), '\n', -1) as skillName, COUNT(*) as count " +
                   "FROM cv c " +
                   "CROSS JOIN (SELECT 1 + units.i + tens.i * 10 n " +
                   "FROM (SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) units, " +
                   "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) tens " +
                   "WHERE 1 + units.i + tens.i * 10 <= (SELECT MAX(LENGTH(skills) - LENGTH(REPLACE(skills, '\n', '')) + 1) FROM cv)) n " +
                   "WHERE SUBSTRING_INDEX(SUBSTRING_INDEX(c.skills, '\n', n.n), '\n', -1) != '' " +
                   "GROUP BY skillName " +
                   "ORDER BY count DESC " +
                   "LIMIT 10", nativeQuery = true)
    List<Map<String, Object>> getTopSkills();
}
