package vn.hstore.jobhunter.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hstore.jobhunter.domain.response.RestResponse;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {
    
    @Value("${hstore.upload-file.base-uri}")
    private String uploadBaseUri;
    
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Xử lý đường dẫn từ file:///D:/Sping_REST/upload/
            String uploadDir = uploadBaseUri.replace("file:///", "");
            
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Tạo tên file ngẫu nhiên để tránh trùng lặp
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Lưu file
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // Trả về URL để truy cập file
            String fileUrl = "/api/uploads/" + newFilename;
            
            RestResponse<Map<String, String>> response = new RestResponse<>();
            response.setStatusCode(200);
            response.setError(null);
            response.setMessage("Upload ảnh thành công");
            
            Map<String, String> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("filePath", filePath.toString());
            response.setData(data);
            
            return ResponseEntity.ok().body(response);
        } catch (IOException e) {
            RestResponse<Object> errorResponse = new RestResponse<>();
            errorResponse.setStatusCode(500);
            errorResponse.setError("Internal Server Error");
            errorResponse.setMessage("Không thể upload ảnh: " + e.getMessage());
            errorResponse.setData(null);
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
} 