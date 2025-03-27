package vn.hstore.jobhunter.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hstore.jobhunter.repository.RoleRepository;
import vn.hstore.jobhunter.repository.SubscriptionPackageRepository;
import vn.hstore.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api")
public class ServerStatusController {

    private final RoleRepository roleRepository;
    private final SubscriptionPackageRepository subscriptionPackageRepository;

    @Autowired
    public ServerStatusController(
            RoleRepository roleRepository, 
            SubscriptionPackageRepository subscriptionPackageRepository) {
        this.roleRepository = roleRepository;
        this.subscriptionPackageRepository = subscriptionPackageRepository;
    }

    @GetMapping("/status")
    @ApiMessage("Check server status")
    public ResponseEntity<Map<String, Object>> getServerStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "online");
        response.put("timestamp", System.currentTimeMillis());
        
        // Thêm thông tin về các roles
        Map<String, Object> roles = new HashMap<>();
        roleRepository.findAll().forEach(role -> {
            roles.put(role.getName(), Map.of(
                "id", role.getId(),
                "description", role.getDescription(),
                "permissions", role.getPermissions().size()
            ));
        });
        response.put("roles", roles);
        
        // Thêm thông tin về các gói đăng ký
        Map<String, Object> packages = new HashMap<>();
        subscriptionPackageRepository.findAll().forEach(pkg -> {
            packages.put("id_" + pkg.getId(), Map.of(
                "name", pkg.getName(),
                "price", pkg.getPrice(),
                "duration", pkg.getDurationDays(),
                "job_limit", pkg.getJobPostLimit(),
                "is_active", pkg.getIsActive()
            ));
        });
        response.put("subscription_packages", packages);
        
        return ResponseEntity.ok(response);
    }
} 