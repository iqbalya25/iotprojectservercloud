package org.example.iotproject.DeviceValue.dto;

import lombok.Data;
import org.example.iotproject.DeviceValue.entity.DeviceValue;

@Data
public class TemperatureLogDTO {
    private Long id;
    private double value1;
    private String deviceName;
    private int masterId;
    private String timestamp;

    public static TemperatureLogDTO fromEntity(DeviceValue deviceValue) {
        TemperatureLogDTO dto = new TemperatureLogDTO();
        dto.setId(deviceValue.getId());
        dto.setValue1(deviceValue.getValue1());
        dto.setDeviceName(deviceValue.getDevice().getDeviceName());
        dto.setMasterId(Math.toIntExact(deviceValue.getMaster().getId()));
        dto.setTimestamp(deviceValue.getCreatedAt().toString());
        return dto;
    }
}