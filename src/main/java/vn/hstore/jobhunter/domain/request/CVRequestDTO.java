package vn.hstore.jobhunter.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CVRequestDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    private String email;
    private String phoneNumber;
    private String address;

    private String education;
    private String experience;
    private String skills;
    private String customContent;
    private Long userId;
    private Long jobId;
}
