package vn.hstore.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.ApiStatistics;

@Repository
public interface ApiStatisticsRepository extends JpaRepository<ApiStatistics, Long> {
} 