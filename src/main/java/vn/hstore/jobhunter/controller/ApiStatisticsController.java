package vn.hstore.jobhunter.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hstore.jobhunter.domain.ApiStatistics;
import vn.hstore.jobhunter.domain.response.RestResponse;
import vn.hstore.jobhunter.service.ApiStatisticsService;
import vn.hstore.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/admin/api-statistics")
public class ApiStatisticsController {

    private final ApiStatisticsService apiStatisticsService;

    public ApiStatisticsController(ApiStatisticsService apiStatisticsService) {
        this.apiStatisticsService = apiStatisticsService;
    }

    @PostMapping
    @ApiMessage("Create API statistics")
    public ResponseEntity<?> createApiStatistics(@RequestBody ApiStatistics statistics) {
        try {
            ApiStatistics created = apiStatisticsService.createApiStatistics(
                statistics.getApiPath(),
                statistics.getMethod(),
                statistics.getModule(),
                statistics.getName()
            );

            RestResponse<ApiStatistics> response = new RestResponse<>();
            response.setStatusCode(200);
            response.setError(null);
            response.setMessage("Tạo API statistics thành công");
            response.setData(created);

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

    @GetMapping
    @ApiMessage("Get all API statistics")
    public ResponseEntity<?> getAllApiStatistics() {
        try {
            List<ApiStatistics> statistics = apiStatisticsService.getAllApiStatistics();

            RestResponse<List<ApiStatistics>> response = new RestResponse<>();
            response.setStatusCode(200);
            response.setError(null);
            response.setMessage("Lấy danh sách API statistics thành công");
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
} 