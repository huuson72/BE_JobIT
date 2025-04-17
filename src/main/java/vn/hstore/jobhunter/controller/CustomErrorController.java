package vn.hstore.jobhunter.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Object> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Map<String, Object> body = new HashMap<>();
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                body.put("statusCode", statusCode);
                body.put("message", "Access Denied");
                body.put("error", "Forbidden");
                return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                body.put("statusCode", statusCode);
                body.put("message", "Resource not found");
                body.put("error", "Not Found");
                return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
            } else {
                body.put("statusCode", statusCode);
                body.put("message", "An error occurred");
                body.put("error", "Error");
                return new ResponseEntity<>(body, HttpStatus.valueOf(statusCode));
            }
        }
        
        body.put("statusCode", 500);
        body.put("message", "Internal Server Error");
        body.put("error", "Error");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 