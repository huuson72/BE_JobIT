package vn.hstore.jobhunter.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseSubscriptionRequest {
    
    @NotNull(message = "UserId không được để trống")
    private Long userId;
    
    @NotNull(message = "CompanyId không được để trống")
    private Long companyId;
    
    @NotNull(message = "PackageId không được để trống")
    private Long packageId;
    
    private String paymentMethod = "online"; // Default payment method
} 