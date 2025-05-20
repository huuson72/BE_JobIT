package vn.hstore.jobhunter.domain;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "cv")
@Getter
@Setter
@Where(clause = "is_deleted = false")
public class CV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title; // Xóa @NotBlank ở đây
    private String profileSummary;

    private String fullName; // Xóa @NotBlank ở đây
    private String email;
    private String phoneNumber;
    private String address;

    private String education;
    private String experience;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String skills;

    @Column(columnDefinition = "LONGTEXT")
    private String customContent;

    private Instant createdAt;
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = true)
    private Job job;

    private boolean active;

    private boolean isDeleted = false;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        this.active = true;
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
    }
}
