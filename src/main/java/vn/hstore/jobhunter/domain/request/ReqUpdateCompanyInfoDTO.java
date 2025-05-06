package vn.hstore.jobhunter.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateCompanyInfoDTO {
    
    @NotNull(message = "ID của HR không được để trống")
    private Long hrId;
    
    @NotNull(message = "ID của công ty không được để trống")
    private Long companyId;
    
    @NotNull(message = "Tên công ty không được để trống")
    private String companyName;
    
    @NotNull(message = "Địa chỉ công ty không được để trống")
    private String companyAddress;
    
    private String companyDescription;
    private String companyLogo;
    private String businessLicense;
} 