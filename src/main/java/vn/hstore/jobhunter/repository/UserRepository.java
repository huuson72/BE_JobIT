package vn.hstore.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Company;
import vn.hstore.jobhunter.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User findByRefreshTokenAndEmail(String token, String email);

    List<User> findByCompany(Company company);

    Optional<User> findById(Long id);

    @Query("SELECT u.role as role, COUNT(u) as count FROM User u GROUP BY u.role")
    List<Map<String, Object>> countUsersByRole();

    @Query("SELECT DATE(u.createdAt) as date, COUNT(u) as count FROM User u WHERE u.createdAt >= :startDate GROUP BY DATE(u.createdAt) ORDER BY date DESC")
    List<Map<String, Object>> countNewUsersByTime(@Param("startDate") Instant startDate);
}
