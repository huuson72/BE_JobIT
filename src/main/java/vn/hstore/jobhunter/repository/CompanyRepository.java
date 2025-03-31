package vn.hstore.jobhunter.repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>,
        JpaSpecificationExecutor<Company> {

    @Query("SELECT DATE(c.createdAt) as date, COUNT(c) as count FROM Company c WHERE c.createdAt >= :startDate GROUP BY DATE(c.createdAt) ORDER BY date DESC")
    List<Map<String, Object>> countNewCompaniesByTime(@Param("startDate") Instant startDate);
}
