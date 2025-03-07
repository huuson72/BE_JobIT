package vn.hstore.jobhunter.service;

import vn.hstore.jobhunter.domain.Company;
import vn.hstore.jobhunter.domain.Review;
import vn.hstore.jobhunter.domain.User;
import vn.hstore.jobhunter.domain.ReviewDTO;
import vn.hstore.jobhunter.repository.CompanyRepository;
import vn.hstore.jobhunter.repository.ReviewRepository;
import vn.hstore.jobhunter.repository.UserRepository;
import vn.hstore.jobhunter.util.SecurityUtil;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    // Constructor injection
    public ReviewService(
            ReviewRepository reviewRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public ReviewDTO addReview(ReviewDTO reviewDTO) {
        // Tìm công ty theo ID, nếu không tìm thấy thì ném ngoại lệ
        Company company = companyRepository.findById(reviewDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Tìm người dùng theo email
        String userEmail = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not logged in"));
        User user = userRepository.findByEmail(userEmail);

        // Kiểm tra xem người dùng có tồn tại không
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Tạo đối tượng Review và lưu vào database
        Review review = new Review();
        review.setContent(reviewDTO.getContent());
        review.setRating(reviewDTO.getRating());
        review.setCompany(company);
        review.setUser(user);

        Review savedReview = reviewRepository.save(review);

        // Trả về ReviewDTO với thông tin người dùng
        ReviewDTO responseDTO = new ReviewDTO();
        responseDTO.setContent(savedReview.getContent());
        responseDTO.setRating(savedReview.getRating());
        responseDTO.setCompanyId(savedReview.getCompany().getId());
        responseDTO.setUserName(savedReview.getUser().getName()); // Thêm tên người dùng

        return responseDTO;
    }

    public List<ReviewDTO> getReviewsByCompany(long companyId) {
        return reviewRepository.findByCompanyId(companyId).stream()
                .map(review -> {
                    ReviewDTO dto = new ReviewDTO();
                    dto.setContent(review.getContent());
                    dto.setRating(review.getRating());
                    dto.setCompanyId(review.getCompany().getId());
                    dto.setUserName(review.getUser().getName()); // Thêm tên người dùng
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
