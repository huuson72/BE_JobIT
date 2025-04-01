package vn.hstore.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hstore.jobhunter.domain.response.RestResponse;
import vn.hstore.jobhunter.domain.response.admin.RevenueStatisticsDTO;
import vn.hstore.jobhunter.service.RevenueStatisticsService;
import vn.hstore.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/test-revenue")
public class TestRevenueController {

    private final RevenueStatisticsService revenueStatisticsService;

    public TestRevenueController(RevenueStatisticsService revenueStatisticsService) {
        this.revenueStatisticsService = revenueStatisticsService;
    }

    @GetMapping
    @ApiMessage("Test revenue statistics")
    public ResponseEntity<?> testRevenueStatistics() {
        try {
            RevenueStatisticsDTO statistics = revenueStatisticsService.getRevenueStatistics();

            RestResponse<RevenueStatisticsDTO> response = new RestResponse<>();
            response.setStatusCode(200);
            response.setError(null);
            response.setMessage("Lấy thống kê doanh thu thành công");
            response.setData(statistics);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            RestResponse<Object> errorResponse = new RestResponse<>();
            errorResponse.setStatusCode(500);
            errorResponse.setError("Internal Server Error");
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/simple")
    @ApiMessage("Simple test")
    public ResponseEntity<?> simpleTest() {
        RestResponse<String> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setError(null);
        response.setMessage("API test hoạt động");
        response.setData("API test hoạt động bình thường");

        return ResponseEntity.ok().body(response);
    }
} 