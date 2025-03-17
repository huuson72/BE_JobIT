package vn.hstore.jobhunter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hstore.jobhunter.domain.FavouriteJob;
import vn.hstore.jobhunter.domain.Job;
import vn.hstore.jobhunter.domain.User;

@Repository
public interface FavoriteJobRepository extends JpaRepository<FavouriteJob, Long> {

    boolean existsByUserAndJob(User user, Job job);

    void deleteByUserAndJob(User user, Job job);

    List<FavouriteJob> findByUser(User user);

    Optional<FavouriteJob> findByUserAndJob(User user, Job job);
}
