package org.example.iotproject.DeviceValue.repository;

import org.example.iotproject.Device.entity.Device;
import org.example.iotproject.DeviceValue.entity.DeviceValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DeviceValueRepository extends JpaRepository<DeviceValue, Long> {
    // Basic CRUD operations are automatically provided by JpaRepository

    // Optional: You can add custom query methods if needed
    List<DeviceValue> findByDevice(Device device);
    List<DeviceValue> findByDeviceAndCreatedAtBetween(Device device, Instant startTime, Instant endTime);
    List<DeviceValue> findTop10ByDeviceOrderByCreatedAtDesc(Device device);
}
