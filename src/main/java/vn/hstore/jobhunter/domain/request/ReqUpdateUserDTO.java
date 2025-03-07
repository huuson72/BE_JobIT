package vn.hstore.jobhunter.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.hstore.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ReqUpdateUserDTO {

    private String name;

    @NotBlank(message = "Email không được để trống")
    private String email;

    private String password; // Mật khẩu mới (nếu cần cập nhật)

    private int age;

    private GenderEnum gender;

    private String address;

    private Long roleId; // ID của role mới (nếu cần cập nhật)

    private Long companyId; // ID của company mới (nếu cần cập nhật)
}
