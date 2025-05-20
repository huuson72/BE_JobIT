package vn.hstore.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.hstore.jobhunter.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>,
        JpaSpecificationExecutor<Role> {

    boolean existsByName(String name);
    
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.name = :name AND r.isDeleted = false")
    boolean existsByNameAndNotDeleted(@Param("name") String name);

    Role findByName(String name);
    
    @Query("SELECT r FROM Role r WHERE r.name = :name AND r.isDeleted = false")
    Role findByNameAndNotDeleted(@Param("name") String name);
}
