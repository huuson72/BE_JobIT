package vn.hstore.jobhunter.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResFetchResumeDTO {

    private Long id;
    private String email;
    private String status;
    private String url;
    private String createdAt;
    private String updatedAt;
    private String userEmail; // 👈 Thêm email user

    public ResFetchResumeDTO(Resume resume) {
        this.id = resume.getId();
        this.email = resume.getEmail();
        this.status = resume.getStatus().name();
        this.url = resume.getUrl();
        this.createdAt = resume.getCreatedAt().toString();
        this.updatedAt = resume.getUpdatedAt() != null ? resume.getUpdatedAt().toString() : null;
        this.userEmail = resume.getUser() != null ? resume.getUser().getEmail() : null; // 👈 Lấy email từ user
    }
}
