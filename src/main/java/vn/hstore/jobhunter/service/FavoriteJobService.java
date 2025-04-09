package vn.hstore.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.hstore.jobhunter.domain.FavouriteJob;
import vn.hstore.jobhunter.domain.Job;
import vn.hstore.jobhunter.domain.User;
import vn.hstore.jobhunter.repository.FavoriteJobRepository;
import vn.hstore.jobhunter.repository.JobRepository;
import vn.hstore.jobhunter.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FavoriteJobService {

    private final FavoriteJobRepository favoriteJobRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Transactional
    public Job addToFavorites(Long userId, Long jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Kiểm tra kỹ xem đã tồn tại trong danh sách yêu thích của user chưa
        if (favoriteJobRepository.findByUserAndJob(user, job).isPresent()) {
            throw new RuntimeException("Job is already in favorites for this user");
        }

        FavouriteJob favoriteJob = new FavouriteJob();
        favoriteJob.setUser(user);
        favoriteJob.setJob(job);
        favoriteJobRepository.save(favoriteJob);

        return job;
    }

    @Transactional
    public Job removeFromFavorites(Long userId, Long jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        favoriteJobRepository.findByUserAndJob(user, job)
                .ifPresent(favoriteJobRepository::delete);

        return job; // ✅ Trả về Job vừa bị xoá khỏi danh sách yêu thích
    }

    @Transactional
    public Job toggleFavoriteJob(Long userId, Long jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Check if the job is already in favorites
        if (favoriteJobRepository.existsByUserAndJob(user, job)) {
            // If it exists, remove it
            favoriteJobRepository.deleteByUserAndJob(user, job);
            return job;
        } else {
            // If it doesn't exist, add it
            FavouriteJob favoriteJob = new FavouriteJob();
            favoriteJob.setUser(user);
            favoriteJob.setJob(job);
            favoriteJobRepository.save(favoriteJob);
            return job;
        }
    }

    public List<Job> getFavoriteJobs(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FavouriteJob> favoriteJobs = favoriteJobRepository.findByUser(user);
        return favoriteJobs.stream().map(FavouriteJob::getJob).collect(Collectors.toList());
    }
}
