package org.example.iotproject.Mqtt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.Device.entity.Device;
import org.example.iotproject.Device.repository.DeviceRepository;
import org.example.iotproject.DeviceStatus.entity.DeviceStatus;
import org.example.iotproject.DeviceStatus.service.DeviceStatusService;
import org.example.iotproject.Master.entity.Master;
import org.example.iotproject.Master.repository.MasterRepository;
import org.example.iotproject.WebSocket.service.WebSocketService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MqttHandlerService {
    private final WebSocketService webSocketService;
    private final MqttService mqttService;

    public MqttHandlerService(WebSocketService webSocketService, MqttService mqttService) {
        this.webSocketService = webSocketService;
        this.mqttService = mqttService;
    }

    @PostConstruct
    public void subscribeMqttTopics() {
        // Subscribe to device status
        mqttService.subscribe("plc/device/status", (topic, msg) -> {
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
}
