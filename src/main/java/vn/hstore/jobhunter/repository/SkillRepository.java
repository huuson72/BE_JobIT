package vn.hstore.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long>,
        JpaSpecificationExecutor<Skill> {

    boolean existsByName(String name);
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Skill s WHERE s.name = :name AND s.isDeleted = false")
    boolean existsByNameAndNotDeleted(@Param("name") String name);

    List<Skill> findByIdIn(List<Long> id);
}
