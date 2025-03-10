package vn.hstore.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.hstore.jobhunter.domain.Job;
import vn.hstore.jobhunter.domain.Skill;
import vn.hstore.jobhunter.domain.Subscriber;
import vn.hstore.jobhunter.domain.response.email.ResEmailJob;
import vn.hstore.jobhunter.repository.JobRepository;
import vn.hstore.jobhunter.repository.SkillRepository;
import vn.hstore.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(
            SubscriberRepository subscriberRepository,
            SkillRepository skillRepository,
            JobRepository jobRepository,
            EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    // @Scheduled(cron = "*/10 * * * * *")
    // public void testCron() {
    // System.out.println(">>> TEST CRON");
    // }
    public boolean isExistsByEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    @Scheduled(cron = "0 25 11 * * ?")
    @Transactional
    public void sendScheduledEmails() {
        this.sendSubscribersEmailJobs();
    }

    // public Subscriber create(Subscriber subs) {
    //     // check skills
    //     if (subs.getSkills() != null) {
    //         List<Long> reqSkills = subs.getSkills()
    //                 .stream().map(x -> x.getId())
    //                 .collect(Collectors.toList());
    //         List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
    //         subs.setSkills(dbSkills);
    //     }
    //     Subscriber savedSubscriber = this.subscriberRepository.save(subs);
    //     sendSubscribersEmailJobsForUser(savedSubscriber);
    //     return this.subscriberRepository.save(subs);
    // }
    // public void sendSubscribersEmailJobsForUser(Subscriber sub) {
    //     List<Skill> listSkills = sub.getSkills();
    //     if (listSkills != null && !listSkills.isEmpty()) {
    //         List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
    //         if (listJobs != null && !listJobs.isEmpty()) {
    //             List<ResEmailJob> arr = listJobs.stream()
    //                     .map(this::convertJobToSendEmail)
    //                     .collect(Collectors.toList());
    //             this.emailService.sendEmailFromTemplateSync(
    //                     sub.getEmail(),
    //                     "Cơ hội việc làm mới phù hợp với bạn!",
    //                     "job",
    //                     sub.getName(),
    //                     arr);
    //         }
    //     }
    // }
    public Subscriber create(Subscriber subs) {
        // 1️⃣ Kiểm tra kỹ năng của subscriber
        if (subs.getSkills() != null) {
            List<Long> reqSkills = subs.getSkills().stream()
                    .map(Skill::getId)
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subs.setSkills(dbSkills);
        }

        // 2️⃣ Lưu subscriber vào database
        Subscriber savedSubscriber = this.subscriberRepository.save(subs);

        // 3️⃣ Gửi email ngay lập tức nếu có job phù hợp
        sendSubscribersEmailJobsForUser(savedSubscriber);

        // 4️⃣ Trả về subscriber đã lưu
        return savedSubscriber;
    }

    // ✅ Gửi email ngay khi có job phù hợp
    public void sendSubscribersEmailJobsForUser(Subscriber sub) {
        System.out.println("📧 Gửi email cho: " + sub.getEmail());

        try {
            List<Skill> listSkills = sub.getSkills();
            if (listSkills != null && !listSkills.isEmpty()) {
                List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                if (listJobs != null && !listJobs.isEmpty()) {
                    List<ResEmailJob> arr = listJobs.stream()
                            .map(this::convertJobToSendEmail)
                            .collect(Collectors.toList());

                    this.emailService.sendEmailFromTemplateSync(
                            sub.getEmail(),
                            "Danh sách công việc phù hợp với bạn!",
                            "job",
                            sub.getName(),
                            arr);
                } else {
                    System.out.println("⚠ Không có công việc nào phù hợp với ứng viên: " + sub.getEmail());
                }
            } else {
                System.out.println("⚠ Ứng viên không có kỹ năng nào: " + sub.getEmail());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Lỗi khi gửi email cho " + sub.getEmail());
        }
    }

    public Subscriber update(Subscriber subsDB, Subscriber subsRequest) {
        // check skills
        if (subsRequest.getSkills() != null) {
            List<Long> reqSkills = subsRequest.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subsDB.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subsDB);
    }

    public Subscriber findById(long id) {
        Optional<Subscriber> subsOptional = this.subscriberRepository.findById(id);
        if (subsOptional.isPresent()) {
            return subsOptional.get();
        }
        return null;
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<ResEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

    public Subscriber registerAndSendEmail(Subscriber subs) {
        Subscriber savedSubscriber = create(subs);  // Lưu vào DB trước
        sendSubscribersEmailJobsForUser(savedSubscriber); // Gửi mail ngay sau khi đăng ký
        return savedSubscriber;
    }

}
