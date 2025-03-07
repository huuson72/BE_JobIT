package vn.hstore.jobhunter.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    private Long companyId;
    private String content;
    private int rating;
}
