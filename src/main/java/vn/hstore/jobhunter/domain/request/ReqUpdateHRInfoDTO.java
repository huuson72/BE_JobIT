package vn.hstore.jobhunter.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.hstore.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ReqUpdateHRInfoDTO {
    
    // Thông tin cá nhân
    @NotNull(message = "Tên không được để trống")
    private String name;
    
    @NotNull(message = "Địa chỉ không được để trống")
    private String address;
    
    @NotNull(message = "Số điện thoại không được để trống")
    private String phone;
    
    @NotNull(message = "Tuổi không được để trống")
    private Integer age;
    
    @NotNull(message = "Giới tính không được để trống")
    private GenderEnum gender;
    
    private String businessLicense;

    // Thông tin công ty
    @NotNull(message = "Tên công ty không được để trống")
    private String companyName;
    
    @NotNull(message = "Địa chỉ công ty không được để trống")
    private String companyAddress;
    
    private String companyDescription;
    private String companyLogo;
} 