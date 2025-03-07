package vn.hstore.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hstore.jobhunter.domain.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCompanyId(long companyId);
}
