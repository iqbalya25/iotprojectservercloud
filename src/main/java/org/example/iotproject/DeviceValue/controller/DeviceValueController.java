package org.example.iotproject.DeviceValue.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.Device.entity.Device;
import org.example.iotproject.Device.service.DeviceService;
import org.example.iotproject.DeviceValue.dto.TemperatureLogDTO;
import org.example.iotproject.DeviceValue.entity.DeviceValue;
import org.example.iotproject.DeviceValue.repository.DeviceValueRepository;
import org.example.iotproject.DeviceValue.service.DeviceValueService;
import org.hibernate.query.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/temperature")
@CrossOrigin(origins = {"https://iotproject-fe.vercel.app", "http://localhost:3000"})
public class DeviceValueController {
    private final DeviceValueService deviceValueService;
    private final DeviceService deviceService;
    private final DeviceValueRepository deviceValueRepository;

    public DeviceValueController(DeviceValueService deviceValueService, DeviceService deviceService , DeviceValueRepository deviceValueRepository) {
        this.deviceValueService = deviceValueService;
        this.deviceService = deviceService;
        this.deviceValueRepository = deviceValueRepository;
    }

    @GetMapping("/history")
    public ResponseEntity<?> getTemperatureHistory(
            @RequestParam String range,
            @RequestParam(required = false) String type) {

        try {
            Instant endTime = Instant.now();
            Instant startTime = calculateStartTime(range);

            if ("stats".equals(type)) {
                Map<String, Object> stats = deviceValueService.getTemperatureStatistics(startTime, endTime);
                return ResponseEntity.ok(stats);
            } else {
                List<DeviceValue> history = deviceValueService.getTemperatureHistory(startTime, endTime);
                return ResponseEntity.ok(history);
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private Instant calculateStartTime(String range) {
        Instant endTime = Instant.now();
        return switch (range) {
            case "1h" -> endTime.minus(1, ChronoUnit.HOURS);
            case "24h" -> endTime.minus(24, ChronoUnit.HOURS);
            case "7d" -> endTime.minus(7, ChronoUnit.DAYS);
            case "30d" -> endTime.minus(30, ChronoUnit.DAYS);
            default -> throw new IllegalArgumentException("Invalid range: " + range);
        };
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getTemperatureLogs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            // Get date range
            LocalDate selectedDate = date != null ? date : LocalDate.now();
            Instant startTime = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant endTime = selectedDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

            // Use your existing service method
            List<DeviceValue> allLogs = deviceValueService.getTemperatureHistory(startTime, endTime);

            // Sort by timestamp descending (latest first)
            allLogs.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

            // Manual pagination
            int totalItems = allLogs.size();
            int totalPages = (int) Math.ceil((double) totalItems / size);
            int start = page * size;
            int end = Math.min(start + size, totalItems);

            List<DeviceValue> paginatedLogs = allLogs.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("content", paginatedLogs.stream()
                    .map(TemperatureLogDTO::fromEntity)
                    .collect(Collectors.toList()));
            response.put("currentPage", page);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("size", size);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing temperature logs request", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

//
}
