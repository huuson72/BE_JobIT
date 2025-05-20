# Tích hợp bản đồ tương tác vào ứng dụng Spring Boot

## Phương án triển khai

### 1. Kiến trúc tổng quan
- **Backend (Spring Boot)**: Cung cấp API geocoding, lưu trữ vị trí
- **Frontend**: Hiển thị bản đồ sử dụng Leaflet.js

### 2. Thiết lập Backend (Spring Boot)

#### 2.1. Tạo model Location
```java
package vn.hstore.jobhunter.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Double latitude;
    private Double longitude;
    private String description;
}
```

#### 2.2. Tạo Repository
```java
package vn.hstore.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hstore.jobhunter.domain.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByName(String name);
}
```

#### 2.3. Tạo Service Geocoding
```java
package vn.hstore.jobhunter.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.List;

@Service
public class GeocodingService {

    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org/search?format=json&q=";
    
    private final RestTemplate restTemplate;
    
    public GeocodingService() {
        this.restTemplate = new RestTemplate();
    }
    
    public Map<String, Double> geocode(String placeName) {
        String url = NOMINATIM_API + placeName;
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        
        if (response.getBody() != null && !response.getBody().isEmpty()) {
            Map<String, Object> firstResult = (Map<String, Object>) response.getBody().get(0);
            Double lat = Double.parseDouble((String) firstResult.get("lat"));
            Double lon = Double.parseDouble((String) firstResult.get("lon"));
            
            return Map.of("latitude", lat, "longitude", lon);
        }
        
        throw new RuntimeException("Location not found: " + placeName);
    }
}
```

#### 2.4. Tạo Controller
```java
package vn.hstore.jobhunter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hstore.jobhunter.domain.Location;
import vn.hstore.jobhunter.repository.LocationRepository;
import vn.hstore.jobhunter.service.GeocodingService;

import java.util.Map;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private GeocodingService geocodingService;
    
    @GetMapping("/geocode")
    public ResponseEntity<?> geocode(@RequestParam String placeName) {
        try {
            Map<String, Double> coordinates = geocodingService.geocode(placeName);
            return ResponseEntity.ok(coordinates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<?> saveLocation(@RequestBody Location location) {
        // Nếu không có tọa độ, sử dụng geocoding để lấy
        if (location.getLatitude() == null || location.getLongitude() == null) {
            try {
                Map<String, Double> coordinates = geocodingService.geocode(location.getName());
                location.setLatitude(coordinates.get("latitude"));
                location.setLongitude(coordinates.get("longitude"));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Could not geocode location: " + e.getMessage()));
            }
        }
        
        Location savedLocation = locationRepository.save(location);
        return ResponseEntity.ok(savedLocation);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllLocations() {
        return ResponseEntity.ok(locationRepository.findAll());
    }
}
```

### 3. Thiết lập Frontend

#### 3.1. Tạo trang HTML
Tạo file `src/main/resources/static/map.html`:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Bản đồ tương tác</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
    <style>
        #map {
            height: 500px;
            width: 100%;
        }
        #search-container {
            margin: 20px 0;
        }
    </style>
</head>
<body>
    <h1>Bản đồ tương tác</h1>
    
    <div id="search-container">
        <input type="text" id="place-input" placeholder="Nhập tên địa điểm (VD: Hanoi, Hai Duong...)" />
        <button id="search-button">Tìm kiếm</button>
    </div>
    
    <div id="map"></div>
    
    <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
    <script>
        // Khởi tạo bản đồ
        const map = L.map('map').setView([21.0285, 105.8542], 13); // Mặc định: Hà Nội
        
        // Thêm lớp bản đồ OpenStreetMap
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '© OpenStreetMap contributors'
        }).addTo(map);
        
        // Khởi tạo marker (mặc định không hiển thị)
        let marker = null;
        
        // Xử lý sự kiện tìm kiếm
        document.getElementById('search-button').addEventListener('click', searchLocation);
        document.getElementById('place-input').addEventListener('keyup', function(event) {
            if (event.key === "Enter") {
                searchLocation();
            }
        });
        
        function searchLocation() {
            const placeName = document.getElementById('place-input').value.trim();
            if (!placeName) return;
            
            // Gọi API của backend
            fetch(`/api/locations/geocode?placeName=${encodeURIComponent(placeName)}`)
                .then(response => response.json())
                .then(data => {
                    if (data.error) {
                        alert('Không tìm thấy địa điểm: ' + data.error);
                        return;
                    }
                    
                    // Lấy tọa độ
                    const lat = data.latitude;
                    const lng = data.longitude;
                    
                    // Di chuyển bản đồ đến vị trí
                    map.setView([lat, lng], 13);
                    
                    // Xóa marker cũ nếu có
                    if (marker) {
                        map.removeLayer(marker);
                    }
                    
                    // Thêm marker mới
                    marker = L.marker([lat, lng]).addTo(map)
                        .bindPopup(placeName)
                        .openPopup();
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Có lỗi xảy ra khi tìm kiếm địa điểm');
                });
        }
    </script>
</body>
</html>
```

#### 3.2. Tạo Controller để phục vụ trang HTML
```java
package vn.hstore.jobhunter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapViewController {

    @GetMapping("/map")
    public String mapView() {
        return "map";
    }
}
```

## Lưu ý quan trọng

1. **CORS**: Nếu frontend và backend ở domain khác nhau, cần cấu hình CORS
2. **Rate Limiting**: API Nominatim có giới hạn gọi, nên cân nhắc lưu cache
3. **Thư viện khác**: Ngoài Leaflet, có thể dùng Google Maps, Mapbox
4. **API Key**: Một số dịch vụ bản đồ yêu cầu API key

## Ưu điểm so với Folium trong Python

1. **Tích hợp tốt hơn**: Leaflet hoạt động trực tiếp trong browser
2. **Hiệu suất**: JavaScript cho tương tác mượt hơn với người dùng
3. **Cấu trúc rõ ràng**: Phân tách Backend/Frontend

## Nhược điểm

1. **Độ phức tạp cao hơn**: Cần viết code cả frontend & backend
2. **Không có sẵn thư viện trực quan**: Không trực quan như Folium trong Python

## Mở rộng

1. **Lưu địa điểm yêu thích**
2. **Hiển thị nhiều địa điểm cùng lúc**
3. **Thêm các lớp thông tin khác như thời tiết, giao thông** 