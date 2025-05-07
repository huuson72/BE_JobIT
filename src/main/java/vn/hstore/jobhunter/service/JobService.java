package vn.hstore.jobhunter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.hstore.jobhunter.domain.Company;
import vn.hstore.jobhunter.domain.Job;
import vn.hstore.jobhunter.domain.Skill;
import vn.hstore.jobhunter.domain.Subscriber;
import vn.hstore.jobhunter.domain.User;
import vn.hstore.jobhunter.domain.response.ResultPaginationDTO;
import vn.hstore.jobhunter.domain.response.email.ResEmailJob;
import vn.hstore.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hstore.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hstore.jobhunter.repository.CompanyRepository;
import vn.hstore.jobhunter.repository.JobRepository;
import vn.hstore.jobhunter.repository.ResumeRepository;
import vn.hstore.jobhunter.repository.SkillRepository;
import vn.hstore.jobhunter.repository.SubscriberRepository;
import vn.hstore.jobhunter.repository.UserRepository;
import vn.hstore.jobhunter.util.JobSpecification;
import vn.hstore.jobhunter.util.SecurityUtil;
import vn.hstore.jobhunter.util.constant.LevelEnum;
import vn.hstore.jobhunter.util.constant.VerificationStatus;
import vn.hstore.jobhunter.util.error.QuotaExceededException;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;
    private final ResumeRepository resumeRepository;
    private final EmailService emailService;
    private final SubscriberRepository subscriberRepository;
    private final UserRepository userRepository;
    private final EmployerSubscriptionService employerSubscriptionService;

    public JobService(JobRepository jobRepository,
            SkillRepository skillRepository,
            CompanyRepository companyRepository,
            ResumeRepository resumeRepository,
            EmailService emailService,
            SubscriberRepository subscriberRepository,
            UserRepository userRepository,
            EmployerSubscriptionService employerSubscriptionService) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
        this.emailService = emailService;
        this.subscriberRepository = subscriberRepository;
        this.userRepository = userRepository;
        this.employerSubscriptionService = employerSubscriptionService;
    }

    public Optional<Job> fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    @Transactional
    public ResCreateJobDTO create(Job j) throws QuotaExceededException {
        // Lấy thông tin người dùng hiện tại
        String currentUsername = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không có thông tin người dùng hiện tại"));

        User currentUser = userRepository.findByEmail(currentUsername);
        if (currentUser == null) {
            throw new RuntimeException("Không tìm thấy thông tin người dùng");
        }

        // Kiểm tra công ty
        if (j.getCompany() == null || j.getCompany().getId() == null) {
            throw new RuntimeException("Thông tin công ty không hợp lệ");
        }

        // Kiểm tra quyền
        if (!currentUser.getRole().getName().equals("SUPER_ADMIN")) {
            // Nếu không phải SUPER_ADMIN thì kiểm tra quyền HR
            if (currentUser.getCompany() == null || !currentUser.getCompany().getId().equals(j.getCompany().getId())) {
                throw new RuntimeException("Bạn không có quyền tạo công việc cho công ty này. Chỉ HR của công ty mới có quyền đăng tin.");
            }

            if (currentUser.getRole() == null || !currentUser.getRole().getName().equals("HR")) {
                throw new RuntimeException("Bạn không có quyền HR để tạo công việc");
            }
        }

        // Kiểm tra trạng thái xác minh
        if (!currentUser.getRole().getName().equals("SUPER_ADMIN")) {
            if (currentUser.getVerificationStatus() == null || 
                currentUser.getVerificationStatus() != VerificationStatus.VERIFIED) {
                throw new RuntimeException("Tài khoản của bạn chưa được xác minh. Vui lòng đợi admin phê duyệt.");
            }
        }

        // Kiểm tra quota đăng tin
        Map<String, Object> quotaCheck = employerSubscriptionService.checkAndUpdateJobPostingQuota(
                currentUser.getId(), j.getCompany().getId());

        boolean canPost = (boolean) quotaCheck.getOrDefault("canPost", false);
        if (!canPost) {
            throw new QuotaExceededException((String) quotaCheck.get("message"));
        }

        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }

        // check company
        if (j.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(j.getCompany().getId());
            if (cOptional.isPresent()) {
                j.setCompany(cOptional.get());
            }
        }

        // create job
        Job currentJob = this.jobRepository.save(j);
        sendEmailToSubscribers(currentJob);

        // convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        // Add quota information to response
        dto.setQuotaMessage((String) quotaCheck.get("message"));

        return dto;
    }

    private void sendEmailToSubscribers(Job job) {
        List<Skill> jobSkills = job.getSkills();

        if (jobSkills == null || jobSkills.isEmpty()) {
            System.out.println("⚠ Không có kỹ năng nào trong công việc mới, không gửi email.");
            return;
        }

        List<Subscriber> subscribers = subscriberRepository.findSubscribersBySkills(jobSkills);

        System.out.println("📢 Số lượng subscriber phù hợp: " + subscribers.size());

        if (!subscribers.isEmpty()) {
            for (Subscriber sub : subscribers) {
                System.out.println("📩 Đang gửi email đến: " + sub.getEmail());

                List<ResEmailJob> jobList = List.of(convertJobToSendEmail(job));

                emailService.sendEmailFromTemplateSync(
                        sub.getEmail(),
                        "Cơ hội việc làm mới phù hợp với bạn!",
                        "job",
                        sub.getName(),
                        jobList);
            }
        } else {
            System.out.println("⚠ Không có subscriber nào phù hợp với công việc này.");
        }
    }

    public ResUpdateJobDTO update(Job j, Job jobInDB) {

        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            jobInDB.setSkills(dbSkills);
        }

        // check company
        if (j.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(j.getCompany().getId());
            if (cOptional.isPresent()) {
                jobInDB.setCompany(cOptional.get());
            }
        }

        // update correct info
        jobInDB.setName(j.getName());
        jobInDB.setSalary(j.getSalary());
        jobInDB.setQuantity(j.getQuantity());
        jobInDB.setLocation(j.getLocation());
        jobInDB.setLevel(j.getLevel());
        jobInDB.setStartDate(j.getStartDate());
        jobInDB.setEndDate(j.getEndDate());
        jobInDB.setActive(j.isActive());

        // update job
        Job currentJob = this.jobRepository.save(jobInDB);

        // convert response
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    public void delete(long id) {
        this.jobRepository.deleteById(id);
    }

    private ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));

        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> skillEmails = skills.stream()
                .map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());

        res.setSkills(skillEmails);
        return res;
    }

    // public ResultPaginationDTO fetchAll(Specification<Job> spec, Pageable pageable) {
    //     Page<Job> pageUser = this.jobRepository.findAll(spec, pageable);
    //     ResultPaginationDTO rs = new ResultPaginationDTO();
    //     ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
    //     mt.setPage(pageable.getPageNumber() + 1);
    //     mt.setPageSize(pageable.getPageSize());
    //     mt.setPages(pageUser.getTotalPages());
    //     mt.setTotal(pageUser.getTotalElements());
    //     rs.setMeta(mt);
    //     rs.setResult(pageUser.getContent());
    //     return rs;
    // }
    public ResultPaginationDTO fetchAll(Specification<Job> spec, Pageable pageable, LevelEnum level, Double minSalary, Double maxSalary, String location) {
        Specification<Job> finalSpec = spec
                .and(JobSpecification.hasLevel(level))
                .and(JobSpecification.hasSalaryBetween(minSalary, maxSalary))
                .and(JobSpecification.hasLocation(location));

        Page<Job> pageJob = this.jobRepository.findAll(finalSpec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageJob.getTotalPages());
        mt.setTotal(pageJob.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageJob.getContent());

        return rs;
    }

    /**
     * Gets comprehensive statistics about jobs in the system
     *
     * @return Map containing various job statistics
     */
    public Map<String, Object> getJobStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // Get total counts
        statistics.put("totalJobs", jobRepository.countTotalJobs());
        statistics.put("activeJobs", jobRepository.countActiveJobs());

        // Get counts by level
        List<Map<String, Object>> levelCounts = jobRepository.countJobsByLevel();
        statistics.put("jobsByLevel", levelCounts);

        // Get counts by location
        List<Map<String, Object>> locationCounts = jobRepository.countJobsByLocation();
        statistics.put("jobsByLocation", locationCounts);

        // Get average salary
        statistics.put("averageSalary", jobRepository.getAverageSalary());

        // Get top companies with most jobs
        List<Map<String, Object>> companyJobs = jobRepository.countJobsByCompany();
        statistics.put("jobsByCompany", companyJobs);

        return statistics;
    }

    /**
     * Returns the job repository
     *
     * @return the job repository
     */
    public JobRepository getJobRepository() {
        return jobRepository;
    }

    /**
     * Gets the total number of jobs in the system
     *
     * @return total count of jobs
     */
    public long getTotalJobCount() {
        return jobRepository.countTotalJobs();
    }

}
