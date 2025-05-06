package vn.hstore.jobhunter.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateHRCompanyInfoDTO {
    
    @NotNull(message = "Tên công ty không được để trống")
    private String companyName;
    
    @NotNull(message = "Địa chỉ công ty không được để trống")
    private String companyAddress;
    
    private String companyDescription;
    private String companyLogo;
    private String businessLicense;
} 