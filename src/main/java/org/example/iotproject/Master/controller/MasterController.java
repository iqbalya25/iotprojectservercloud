package org.example.iotproject.Master.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.Master.entity.Master;
import org.example.iotproject.Master.repository.MasterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters")
@CrossOrigin(origins = {"https://iotproject-fe.vercel.app", "http://localhost:3000"}, allowCredentials = "true")
@Slf4j
public class MasterController {
    private final MasterRepository masterRepository;

    public MasterController(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllMasters() {
        try {
            log.info("Fetching all masters");
            List<Master> masters = masterRepository.findAll();
            log.info("Found {} masters", masters.size());
            return ResponseEntity.ok(masters);
        } catch (Exception e) {
            log.error("Error fetching masters: ", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to fetch masters",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMasterById(@PathVariable Long id) {
        try {
            log.info("Fetching master with id: {}", id);
            return masterRepository.findById(id)
                    .map(master -> {
                        log.info("Found master: {}", master);
                        return ResponseEntity.ok(master);
                    })
                    .orElseGet(() -> {
                        log.warn("Master not found with id: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Error fetching master with id {}: ", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to fetch master",
                            "message", e.getMessage()
                    ));
        }
    }
}


