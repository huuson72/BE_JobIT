package vn.hstore.jobhunter.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.hstore.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class EmployerRegisterDTO {
    
    // Thông tin người dùng
    @NotBlank(message = "Tên không được để trống")
    private String name;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
    
    @NotNull(message = "Tuổi không được để trống")
    private Integer age;
    
    private String address;
    
    private String phone;
    
    private GenderEnum gender;
    
    // Thông tin công ty
    @NotBlank(message = "Tên công ty không được để trống")
    private String companyName;
    
    private String companyAddress;
    
    private String companyDescription;
    
    private String companyLogo;
} 