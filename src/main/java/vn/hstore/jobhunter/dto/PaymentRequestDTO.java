package vn.hstore.jobhunter.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long userId;
    private Long companyId;
    private Long packageId;
    private double amount;
    private double originalAmount;
    private String orderInfo;
    private String orderType;
    private String promotionCode;
} 