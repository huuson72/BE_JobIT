package vn.hstore.jobhunter.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDTO {

    private String content;
    private int rating;
    private long companyId;
    private String userName; // Thêm trường userName
}
