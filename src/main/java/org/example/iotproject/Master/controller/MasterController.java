package org.example.iotproject.Master.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.Master.entity.Master;
import org.example.iotproject.Master.repository.MasterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters")
@CrossOrigin(origins = {"https://iotproject-fe.vercel.app", "http://localhost:3000"})
@Slf4j
public class MasterController {
    private final MasterRepository masterRepository;

    public MasterController(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllMasters() {
        try {
            log.info("Starting to fetch all masters");

            // Test database connection
            boolean isConnected = testDatabaseConnection();
            if (!isConnected) {
                log.error("Database connection failed");
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Database connection failed"));
            }

            // Fetch masters
            List<Master> masters = masterRepository.findAll();
            log.info("Successfully fetched {} masters", masters.size());

            return ResponseEntity.ok(masters);
        } catch (Exception e) {
            log.error("Error fetching masters: ", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to fetch masters",
                            "message", e.getMessage(),
                            "details", e.getClass().getName()
                    ));
        }
    }

    private boolean testDatabaseConnection() {
        try {
            // Try a simple database operation
            masterRepository.count();
            return true;
        } catch (Exception e) {
            log.error("Database connection test failed: ", e);
            return false;
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();

        try {
            // Log environment variables (don't log actual password)
            log.info("Database URL: {}", System.getenv("SPRING_DATASOURCE_URL"));
            log.info("Database Username: {}", System.getenv("SPRING_DATASOURCE_USERNAME"));
            log.info("Database URL exists: {}", System.getenv("SPRING_DATASOURCE_URL") != null);
            log.info("Username exists: {}", System.getenv("SPRING_DATASOURCE_USERNAME") != null);
            log.info("Password exists: {}", System.getenv("SPRING_DATASOURCE_PASSWORD") != null);

            // Test database connection
            masterRepository.count();
            response.put("status", "healthy");
            response.put("message", "Database connection successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Health check failed: ", e);
            response.put("status", "unhealthy");
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getName());
            // Get root cause
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            response.put("rootCause", rootCause.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> checkConfig() {
        Map<String, String> config = new HashMap<>();

        // Get database URL (mask sensitive parts)
        String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (dbUrl != null) {
            // Mask password if present in URL
            dbUrl = dbUrl.replaceAll("password=.*?[&;]", "password=*****&");
            config.put("database_url_set", "true");
            config.put("database_url_pattern", dbUrl.matches("jdbc:postgresql://.*") ? "valid" : "invalid");
        } else {
            config.put("database_url_set", "false");
        }

        // Check username
        config.put("username_set", System.getenv("SPRING_DATASOURCE_USERNAME") != null ? "true" : "false");

        // Check password (don't show actual password)
        config.put("password_set", System.getenv("SPRING_DATASOURCE_PASSWORD") != null ? "true" : "false");

        return ResponseEntity.ok(config);
    }
}

