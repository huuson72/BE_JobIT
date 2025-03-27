package vn.hstore.jobhunter.domain;

import java.time.Instant;
import java.time.LocalDate;

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
@Table(name = "job_posting_usage")
@Getter
@Setter
public class JobPostingUsage {

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
    
    @NotNull(message = "Ngày đăng không được để trống")
    private LocalDate postingDate;
    
    @NotNull(message = "Số lượt đã sử dụng không được để trống")
    private Integer usedCount = 0;
    
    // Mặc định mỗi nhà tuyển dụng có 3 lượt đăng miễn phí mỗi ngày
    @NotNull(message = "Giới hạn miễn phí không được để trống")
    private Integer freeLimit = 3;
    
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