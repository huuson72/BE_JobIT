package vn.hstore.jobhunter.domain;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.hstore.jobhunter.util.SecurityUtil;

@Entity
@Table(name = "employer_subscriptions")
@Getter
@Setter
public class EmployerSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "User không được để trống")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    @NotNull(message = "Company không được để trống")
    private Company company;
    
    @ManyToOne
    @JoinColumn(name = "subscription_package_id")
    @NotNull(message = "Gói đăng ký không được để trống")
    private SubscriptionPackage subscriptionPackage;
    
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private Instant startDate;
    
    @NotNull(message = "Ngày kết thúc không được để trống")
    private Instant endDate;
    
    private Integer remainingPosts;
    
    private Boolean isActive = true;
    
    private String transactionId;
    
    private String paymentMethod;
    
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() 
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() 
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.updatedAt = Instant.now();
    }
} 