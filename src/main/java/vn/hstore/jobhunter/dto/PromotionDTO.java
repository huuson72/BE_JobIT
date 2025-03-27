package vn.hstore.jobhunter.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PromotionDTO {
    private Long id;
    private String name;
    private String description;
    private Double discountPercentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private String code;
    private Long subscriptionPackageId;
} 