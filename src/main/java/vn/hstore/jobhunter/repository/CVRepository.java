package vn.hstore.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.CV;

@Repository
public interface CVRepository extends JpaRepository<CV, Long> {

    List<CV> findByUserId(Long userId);
}
