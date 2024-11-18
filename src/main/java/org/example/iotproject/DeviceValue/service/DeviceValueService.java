package org.example.iotproject.DeviceValue.service;

import org.example.iotproject.Device.entity.Device;
import org.example.iotproject.DeviceValue.entity.DeviceValue;
import org.hibernate.query.Page;

import java.awt.print.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface DeviceValueService {
    public DeviceValue saveDeviceValue(DeviceValue deviceValue);
    List<DeviceValue> getTemperatureHistory(Instant startTime, Instant endTime);
    List<DeviceValue> getLatestTemperatures(int limit);
    Map<String, Object> getTemperatureStatistics(Instant startTime, Instant endTime);
    List<DeviceValue> getDeviceHistory(Device device, Instant startTime, Instant endTime);

    List<DeviceValue> getTemperatureLogsPaginated(
            Instant startTime,
            Instant endTime,
            Pageable pageable
    );
}
