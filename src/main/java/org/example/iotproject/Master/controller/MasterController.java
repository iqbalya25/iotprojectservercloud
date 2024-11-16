package org.example.iotproject.Master.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.Master.dto.MasterDTO;
import org.example.iotproject.Master.entity.Master;
import org.example.iotproject.Master.repository.MasterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

            // Fetch masters and convert to DTOs
            List<Master> masters = masterRepository.findAll();
            List<MasterDTO> masterDTOs = masters.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            log.info("Successfully fetched {} masters", masterDTOs.size());

            return ResponseEntity.ok(masterDTOs);
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

    private MasterDTO convertToDTO(Master master) {
        MasterDTO dto = new MasterDTO();
        dto.setId(master.getId());
        dto.setMasterName(master.getMasterName());
        dto.setMasterIpAddress(master.getMasterIpAddress());
        dto.setMasterPort(master.getMasterPort());
        dto.setPlcId(master.getPlcId());
        dto.setMasterLocation(master.getMasterLocation());
        return dto;
    }

    private boolean testDatabaseConnection() {
        try {
            masterRepository.count();
            return true;
        } catch (Exception e) {
            log.error("Database connection test failed: ", e);
            return false;
        }
    }
}


