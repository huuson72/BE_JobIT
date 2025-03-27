package vn.hstore.jobhunter.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.hstore.jobhunter.domain.Permission;
import vn.hstore.jobhunter.domain.Role;
import vn.hstore.jobhunter.domain.User;
import vn.hstore.jobhunter.service.UserService;
import vn.hstore.jobhunter.util.SecurityUtil;
import vn.hstore.jobhunter.util.error.PermissionException;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // Kiểm tra xem endpoint có cần xác thực không
        if (path != null && (path.startsWith("/api/v1/auth/")
                || path.startsWith("/api/v1/companies/")
                || path.startsWith("/api/v1/jobs/")
                || path.startsWith("/api/v1/skills/")
                || path.startsWith("/api/v1/gencv/export/")
                || path.startsWith("/storage/")
                || path.equals("/")
                || path.equals("/api/v1/files")
                || path.startsWith("/v3/api-docs/")
                || path.startsWith("/swagger-ui/")
                || path.equals("/swagger-ui.html")
                || requestURI.contains("/api/v1/gencv/export/"))) {
            return true;
        }

        // Nếu không có token, cho phép truy cập các endpoint công khai
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (email == null || email.isEmpty()) {
            // Cho phép truy cập nếu là endpoint export PDF
            if (requestURI.contains("/api/v1/gencv/export/")) {
                return true;
            }
            return true;
        }

        // check permission
        User user = this.userService.handleGetUserByUsername(email);
        if (user != null) {
            Role role = user.getRole();
            if (role != null) {
                List<Permission> permissions = role.getPermissions();
                boolean isAllow = permissions.stream().anyMatch(item -> item.getApiPath().equals(path)
                        && item.getMethod().equals(httpMethod));

                if (isAllow == false) {
                    // Cho phép truy cập nếu là endpoint export PDF
                    if (requestURI.contains("/api/v1/gencv/export/")) {
                        return true;
                    }
                    throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
                }
            }
        }

        return true;
    }
}
