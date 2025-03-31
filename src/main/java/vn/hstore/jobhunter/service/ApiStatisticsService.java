package vn.hstore.jobhunter.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import vn.hstore.jobhunter.domain.ApiStatistics;
import vn.hstore.jobhunter.repository.ApiStatisticsRepository;

@Service
public class ApiStatisticsService {

    private final ApiStatisticsRepository apiStatisticsRepository;

    public ApiStatisticsService(ApiStatisticsRepository apiStatisticsRepository) {
        this.apiStatisticsRepository = apiStatisticsRepository;
    }

    public ApiStatistics createApiStatistics(String apiPath, String method, String module, String name) {
        ApiStatistics statistics = new ApiStatistics();
        statistics.setApiPath(apiPath);
        statistics.setMethod(method);
        statistics.setModule(module);
        statistics.setName(name);
        statistics.setCreatedAt(Instant.now());
        return apiStatisticsRepository.save(statistics);
    }

    public List<ApiStatistics> getAllApiStatistics() {
        return apiStatisticsRepository.findAll();
    }
} 