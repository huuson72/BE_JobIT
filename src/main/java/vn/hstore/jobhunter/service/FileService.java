package vn.hstore.jobhunter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Value("${hstore.upload-file.base-uri}")
    private String baseURI;

    public void createDirectory(String folder) throws URISyntaxException {
        try {
            // Xử lý URI
            String folderPath = baseURI.replace("file:///", "/") + folder;
            File directory = new File(folderPath);
            
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + directory.getAbsolutePath());
                } else {
                    System.err.println(">>> FAILED TO CREATE DIRECTORY: " + directory.getAbsolutePath());
                }
            } else {
                System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS: " + directory.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println(">>> ERROR CREATING DIRECTORY: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String store(MultipartFile file, String folder) throws URISyntaxException, IOException {
        // Đảm bảo thư mục tồn tại
        createDirectory(folder);
        
        // Tạo tên file duy nhất
        String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        // Kiểm tra folder có dấu / ở cuối không
        if (!folder.endsWith("/")) {
            folder = folder + "/";
        }

        // Tạo đường dẫn tuyệt đối đến file
        String filePath = baseURI.replace("file:///", "/") + folder + finalName;
        File destFile = new File(filePath);
        
        // Đảm bảo thư mục cha tồn tại
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println(">>> FILE STORED SUCCESSFULLY AT: " + destFile.getAbsolutePath());
        }
        
        return finalName;
    }

    public long getFileLength(String fileName, String folder) throws URISyntaxException {
        if (!folder.endsWith("/")) {
            folder = folder + "/";
        }
        
        String filePath = baseURI.replace("file:///", "/") + folder + fileName;
        File file = new File(filePath);

        // file không tồn tại, hoặc file là 1 directory => return 0
        if (!file.exists() || file.isDirectory()) {
            return 0;
        }
        return file.length();
    }

    public InputStreamResource getResource(String fileName, String folder)
            throws URISyntaxException, FileNotFoundException {
        if (!folder.endsWith("/")) {
            folder = folder + "/";
        }
        
        String filePath = baseURI.replace("file:///", "/") + folder + fileName;
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        
        return new InputStreamResource(new FileInputStream(file));
    }
}
