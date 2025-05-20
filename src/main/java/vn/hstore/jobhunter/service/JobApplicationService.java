package vn.hstore.jobhunter.service;

import org.springframework.stereotype.Service;
import java.util.Optional;

import vn.hstore.jobhunter.domain.Job;
import vn.hstore.jobhunter.domain.response.job.JobApplicationsCountDTO;
import vn.hstore.jobhunter.repository.CVRepository;
import vn.hstore.jobhunter.repository.JobRepository;
import vn.hstore.jobhunter.repository.ResumeRepository;
import vn.hstore.jobhunter.util.error.IdInvalidException;

@Service
public class JobApplicationService {

    private final JobRepository jobRepository;
    private final CVRepository cvRepository;
    private final ResumeRepository resumeRepository;

    public JobApplicationService(
            JobRepository jobRepository,
            CVRepository cvRepository,
            ResumeRepository resumeRepository) {
        this.jobRepository = jobRepository;
        this.cvRepository = cvRepository;
        this.resumeRepository = resumeRepository;
    }

    /**
     * Đếm số lượng ứng viên đã nộp CV vào một công việc
     *
     * @param jobId ID của công việc cần đếm số ứng viên
     * @return DTO chứa thông tin số lượng ứng viên
     * @throws IdInvalidException nếu không tìm thấy công việc với ID đã cho
     */
    public JobApplicationsCountDTO countJobApplications(Long jobId) throws IdInvalidException {
        // Kiểm tra tồn tại của job
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("Không tìm thấy công việc với ID = " + jobId);
        }

        Job job = jobOptional.get();
        long cvCount = cvRepository.countByJobId(jobId);
        long resumeCount = resumeRepository.countByJobId(jobId);

        JobApplicationsCountDTO result = new JobApplicationsCountDTO();
        result.setJobId(jobId);
        result.setJobName(job.getName());
        result.setCvCount(cvCount);
        result.setResumeCount(resumeCount);
        result.setTotalApplications(cvCount + resumeCount);

        return result;
    }
} 