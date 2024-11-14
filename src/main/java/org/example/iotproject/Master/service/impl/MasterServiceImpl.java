package org.example.iotproject.Master.service.impl;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.example.iotproject.Address.entity.Address;
import org.example.iotproject.Address.repository.AddressRepository;
import org.example.iotproject.Device.entity.Device;
import org.example.iotproject.Device.service.DeviceService;
import org.example.iotproject.DeviceStatus.entity.DeviceStatus;
import org.example.iotproject.Master.entity.Master;
import org.example.iotproject.Master.repository.MasterRepository;
import org.example.iotproject.Master.service.MasterService;
import org.example.iotproject.Mqtt.service.MqttService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MasterServiceImpl implements MasterService {
    private final AddressRepository addressRepository;
    private final MasterRepository masterRepository;
    private final DeviceService deviceService;
    private final MqttService mqttService;

    private TCPMasterConnection connection;
    private int slaveId;


    public MasterServiceImpl(AddressRepository addressRepository, MasterRepository masterRepository, DeviceService deviceService, MqttService mqttService) {
        this.addressRepository = addressRepository;
        this.masterRepository = masterRepository;
        this.deviceService = deviceService;
        this.mqttService = mqttService;
    }


}


