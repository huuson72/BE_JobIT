package vn.hstore.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hstore.jobhunter.domain.DashboardStatsDTO;
import vn.hstore.jobhunter.service.DashboardService;

@RestController
@RequestMapping("/api/v1/admin")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard-stats")
    public DashboardStatsDTO getDashboardStats() {
        return dashboardService.getDashboardStats();
    }
}
