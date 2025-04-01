package vn.hstore.jobhunter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hstore.jobhunter.domain.ReviewDTO;
import vn.hstore.jobhunter.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // @PostMapping
    // public ResponseEntity<ReviewDTO> addReview(@RequestBody ReviewDTO reviewDTO) {
    //     return ResponseEntity.ok(reviewService.addReview(reviewDTO));
    // }
    @PostMapping
    @PreAuthorize("isAuthenticated()") // 🔥 Chặn người dùng chưa đăng nhập
    public ResponseEntity<ReviewDTO> addReview(@RequestBody ReviewDTO reviewDTO) {
        ReviewDTO savedReview = reviewService.addReview(reviewDTO);
        return ResponseEntity.ok(savedReview);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByCompany(
            @PathVariable(name = "companyId") Long companyId) {
        return ResponseEntity.ok(reviewService.getReviewsByCompany(companyId));
    }
}
