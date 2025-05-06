package vn.hstore.jobhunter.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateHRCompanyDTO {
    
    @NotNull(message = "ID của HR không được để trống")
    private Long hrId;
    
    @NotNull(message = "ID của công ty không được để trống")
    private Long companyId;
    
    private String name;
    private String description;
    private String address;
    private String logo;
} 