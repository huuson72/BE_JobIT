package vn.hstore.jobhunter.domain.response.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewResponse {
    private String userName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
