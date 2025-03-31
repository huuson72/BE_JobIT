package vn.hstore.jobhunter.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "api_statistics")
@Getter
@Setter
public class ApiStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_path")
    private String apiPath;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "method")
    private String method;

    @Column(name = "module")
    private String module;

    @Column(name = "name")
    private String name;
} 