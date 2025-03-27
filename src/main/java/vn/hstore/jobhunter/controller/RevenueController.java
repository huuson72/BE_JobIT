package vn.hstore.jobhunter.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.hstore.jobhunter.domain.Transaction;
import vn.hstore.jobhunter.repository.TransactionRepository;
import vn.hstore.jobhunter.repository.SubscriptionRepository;
import vn.hstore.jobhunter.domain.Subscription;
import vn.hstore.jobhunter.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/revenue")
public class RevenueController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<?>> getRevenueSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Transaction> transactions;
            if (startDate != null && endDate != null) {
                transactions = transactionRepository.findByCreatedAtBetweenAndStatus(startDate, endDate, "SUCCESS");
            } else {
                transactions = transactionRepository.findByStatus("SUCCESS");
            }

            double totalRevenue = transactions.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            Map<String, Long> packageSales = transactions.stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getSubscriptionPackage().getName(),
                            Collectors.counting()));

            Map<String, Double> packageRevenue = transactions.stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getSubscriptionPackage().getName(),
                            Collectors.summingDouble(Transaction::getAmount)));

            return ResponseEntity.ok(new ApiResponse<>(true, "Revenue summary retrieved successfully", Map.of(
                    "totalRevenue", totalRevenue,
                    "packageSales", packageSales,
                    "packageRevenue", packageRevenue
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<Map<String, Object>>(false, "Failed to get revenue summary: " + e.getMessage(), null));
        }
    }

    @GetMapping("/active-subscriptions")
    public ResponseEntity<ApiResponse<?>> getActiveSubscriptions() {
        try {
            List<Subscription> activeSubscriptions = subscriptionRepository.findByStatus("ACTIVE");
            return ResponseEntity.ok(new ApiResponse<List<Subscription>>(true, "Active subscriptions retrieved successfully", activeSubscriptions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<List<Subscription>>(false, "Failed to get active subscriptions: " + e.getMessage(), null));
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<?>> getTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String status) {
        try {
            List<Transaction> transactions;
            if (startDate != null && endDate != null) {
                if (status != null) {
                    transactions = transactionRepository.findByCreatedAtBetweenAndStatus(startDate, endDate, status);
                } else {
                    transactions = transactionRepository.findByCreatedAtBetween(startDate, endDate);
                }
            } else if (status != null) {
                transactions = transactionRepository.findByStatus(status);
            } else {
                transactions = transactionRepository.findAll();
            }

            return ResponseEntity.ok(new ApiResponse<List<Transaction>>(true, "Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<List<Transaction>>(false, "Failed to get transactions: " + e.getMessage(), null));
        }
    }
} 