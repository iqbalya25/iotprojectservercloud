package org.example.iotproject.Mqtt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.Device.entity.Device;
import org.example.iotproject.Device.repository.DeviceRepository;
import org.example.iotproject.Device.service.impl.DeviceServiceImpl;
import org.example.iotproject.DeviceStatus.entity.DeviceStatus;
import org.example.iotproject.DeviceStatus.service.DeviceStatusService;
import org.example.iotproject.DeviceValue.entity.DeviceValue;
import org.example.iotproject.DeviceValue.service.impl.DeviceValueServiceImpl;
import org.example.iotproject.Master.entity.Master;
import org.example.iotproject.Master.repository.MasterRepository;
import org.example.iotproject.WebSocket.service.WebSocketService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class MqttHandlerService {
    private final WebSocketService webSocketService;
    private final MqttService mqttService;
    private final DeviceServiceImpl deviceServiceImpl;
    private final DeviceValueServiceImpl deviceValueServiceImpl;
    private final MasterRepository masterRepository;

    public MqttHandlerService(WebSocketService webSocketService, MqttService mqttService, DeviceServiceImpl deviceServiceImpl, DeviceValueServiceImpl deviceValueServiceImpl, MasterRepository masterRepository) {
        this.webSocketService = webSocketService;
        this.mqttService = mqttService;
        this.deviceServiceImpl = deviceServiceImpl;
        this.deviceValueServiceImpl = deviceValueServiceImpl;
        this.masterRepository = masterRepository;
    }

    @PostConstruct
    public void subscribeMqttTopics() {
        // Subscribe to device status
        mqttService.subscribe("plc/blower/status", (topic, msg) -> {
            try {
                String message = new String(msg.getPayload());
                webSocketService.sendDeviceStatus(message);
                log.info("Received MQTT device status and forwarded to WebSocket");
            } catch (Exception e) {
                log.error("Error handling MQTT device status message", e);
            }
        });

        // Subscribe to connection status
        mqttService.subscribe("plc/connection/status", (topic, msg) -> {
            try {
                String message = new String(msg.getPayload());
                webSocketService.sendConnectionStatus(message);
                log.info("Received MQTT connection status and forwarded to WebSocket");
            } catch (Exception e) {
                log.error("Error handling MQTT connection status message", e);
            }
        });

        mqttService.subscribe("plc/temperature/value", (topic, msg) -> {
            try {
                String message = new String(msg.getPayload());
                JsonNode tempData = new ObjectMapper().readTree(message);

                // Create and save device value
                DeviceValue deviceValue = new DeviceValue();

                // Get master
                Master master = masterRepository.findByPlcId(tempData.get("masterId").asInt());
                deviceValue.setMaster(master);

                // Get device (temperature sensor)
                Device device = deviceServiceImpl.getDeviceByName("Temp_1");
                deviceValue.setDevice(device);

                // Set values
                deviceValue.setValue1(tempData.get("value").asDouble());

                // Save to database
                deviceValueServiceImpl.saveDeviceValue(deviceValue);
                log.info("Temperature value saved to database: {}", tempData.get("value").asDouble());

                // Forward to WebSocket
                webSocketService.sendTemperatureUpdate(message);
                log.info("Temperature value forwarded to WebSocket");
            } catch (Exception e) {
                log.error("Error handling temperature message", e);
            }
        });
    }

    // Receive command from WebSocket and forward to MQTT
    @MessageMapping("/device/command")
    public void receiveCommandAndPublishToMqtt(String command) {
        try {
            mqttService.publish("plc/commands/blower", command);
            log.info("Received WebSocket command and published to MQTT");
        } catch (Exception e) {
            log.error("Error forwarding command to MQTT", e);
        }
    }

//    @MessageMapping("/device/command")
//    public void handleCommand(String commandJson) {
//        log.info("Received WebSocket command: {}", commandJson);
//
//        try {
//            JsonNode command = new ObjectMapper().readTree(commandJson);
//            String action = command.get("action").asText();
//            log.info("Processing action: {}", action);
//
//            switch (action) {
//                case "CONNECT_MASTER":
//                    String ipAddress = command.get("ipAddress").asText();
//                    log.info("Publishing connect command to MQTT. IP: {}", ipAddress);
//                    mqttService.publish("plc/commands/connect",
//                            Map.of("action", "CONNECT_MASTER", "ipAddress", ipAddress));
//                    break;
//                case "DISCONNECT_MASTER":
//                    log.info("Publishing disconnect command to MQTT");
//                    mqttService.publish("plc/commands/connect",
//                            Map.of("action", "DISCONNECT_MASTER"));
//                    break;
//                case "TURN_ON_BLOWER":
//                case "TURN_OFF_BLOWER":
//                    log.info("Publishing blower command to MQTT: {}", action);
//                    mqttService.publish("plc/commands/blower",
//                            Map.of("action", action));
//                    break;
//                default:
//                    log.warn("Unknown command: {}", action);
//            }
//        } catch (Exception e) {
//            log.error("Error handling command: {}", commandJson, e);
//        }
//    }
}
