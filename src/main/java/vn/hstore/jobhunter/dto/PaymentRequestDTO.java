package vn.hstore.jobhunter.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long packageId;
    private Long userId;
    private Long companyId;
    private String orderType;
    private String orderInfo;
    private Long amount;
} 