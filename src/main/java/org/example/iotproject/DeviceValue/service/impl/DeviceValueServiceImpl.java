package org.example.iotproject.DeviceValue.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.Device.entity.Device;
import org.example.iotproject.Device.service.DeviceService;
import org.example.iotproject.DeviceValue.entity.DeviceValue;
import org.example.iotproject.DeviceValue.repository.DeviceValueRepository;
import org.example.iotproject.DeviceValue.service.DeviceValueService;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DeviceValueServiceImpl implements DeviceValueService {
    private final DeviceValueRepository deviceValueRepository;
    private final DeviceService deviceService;

    public DeviceValueServiceImpl(DeviceValueRepository deviceValueRepository, DeviceService deviceService) {
        this.deviceValueRepository = deviceValueRepository;
        this.deviceService = deviceService;
    }

    @Override
    public DeviceValue saveDeviceValue(DeviceValue deviceValue) {
        try {
            return deviceValueRepository.save(deviceValue);
        } catch (Exception e) {
            log.error("Error saving device value", e);
            throw e;
        }
    }

    @Override
    public List<DeviceValue> getTemperatureHistory(Instant startTime, Instant endTime) {
        try {
            log.info("Fetching temperature history from {} to {}", startTime, endTime);
            Device tempSensor = deviceService.getDeviceByName("Temp_1");
            return deviceValueRepository.findByDeviceAndCreatedAtBetweenOrderByCreatedAtAsc(
                    tempSensor, startTime, endTime
            );
        } catch (Exception e) {
            log.error("Error fetching temperature history", e);
            throw new RuntimeException("Failed to fetch temperature history", e);
        }
    }

    @Override
    public List<DeviceValue> getLatestTemperatures(int limit) {
        try {
            log.info("Fetching latest {} temperature readings", limit);
            Device tempSensor = deviceService.getDeviceByName("Temp_1");
            return deviceValueRepository.findTop10ByDeviceOrderByCreatedAtDesc(tempSensor);
        } catch (Exception e) {
            log.error("Error fetching latest temperatures", e);
            throw new RuntimeException("Failed to fetch latest temperatures", e);
        }
    }

    @Override
    public Map<String, Object> getTemperatureStatistics(Instant startTime, Instant endTime) {
        try {
            log.info("Calculating temperature statistics from {} to {}", startTime, endTime);
            List<DeviceValue> values = getTemperatureHistory(startTime, endTime);

            Map<String, Object> stats = new HashMap<>();
            if (!values.isEmpty()) {
                // Calculate min temperature
                double minTemp = values.stream()
                        .mapToDouble(DeviceValue::getValue1)
                        .min()
                        .orElse(0.0);

                // Calculate max temperature
                double maxTemp = values.stream()
                        .mapToDouble(DeviceValue::getValue1)
                        .max()
                        .orElse(0.0);

                // Calculate average temperature
                double avgTemp = values.stream()
                        .mapToDouble(DeviceValue::getValue1)
                        .average()
                        .orElse(0.0);

                stats.put("minimum", minTemp);
                stats.put("maximum", maxTemp);
                stats.put("average", avgTemp);
                stats.put("count", values.size());
                stats.put("startTime", startTime);
                stats.put("endTime", endTime);
            }

            return stats;
        } catch (Exception e) {
            log.error("Error calculating temperature statistics", e);
            throw new RuntimeException("Failed to calculate temperature statistics", e);
        }
    }

    @Override
    public List<DeviceValue> getDeviceHistory(Device device, Instant startTime, Instant endTime) {
        try {
            log.info("Fetching history for device {} from {} to {}", device.getDeviceName(), startTime, endTime);
            return deviceValueRepository.findByDeviceAndCreatedAtBetweenOrderByCreatedAtAsc(
                    device, startTime, endTime
            );
        } catch (Exception e) {
            log.error("Error fetching device history", e);
            throw new RuntimeException("Failed to fetch device history", e);
        }
    }

    @Override
    public List<DeviceValue> getTemperatureLogsPaginated(Instant startTime, Instant endTime, Pageable pageable) {
        return List.of();
    }
}
