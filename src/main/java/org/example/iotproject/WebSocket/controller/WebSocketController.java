package org.example.iotproject.WebSocket.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.Mqtt.service.MqttService;
import org.example.iotproject.WebSocket.dto.CommandDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@Slf4j
public class WebSocketController {
    private final MqttService mqttService;
    private final ObjectMapper objectMapper;

    public WebSocketController(MqttService mqttService, ObjectMapper objectMapper) {
        this.mqttService = mqttService;
        this.objectMapper = objectMapper;
    }

    @MessageMapping("/device/command")
    public void handleCommand(@Payload CommandDto command) {  // Changed from String to CommandDto///
        log.info("Received WebSocket command: {}", command);

        try {
            String action = command.getAction();
            log.info("Processing action: {}", action);

            switch (action) {
                case "CONNECT_MASTER":
                    String ipAddress = command.getIpAddress();
                    log.info("Publishing connect command to MQTT. IP: {}", ipAddress);
                    mqttService.publish("plc/commands/connect",
                            Map.of("action", "CONNECT_MASTER", "ipAddress", ipAddress));
                    break;

                case "DISCONNECT_MASTER":
                    log.info("Publishing disconnect command to MQTT");
                    mqttService.publish("plc/commands/connect",
                            Map.of("action", "DISCONNECT_MASTER"));
                    break;

                case "TURN_ON_BLOWER":
                case "TURN_OFF_BLOWER":
                    log.info("Publishing blower command to MQTT: {}", action);
                    mqttService.publish("plc/commands/blower",
                            Map.of("action", action));
                    break;

                default:
                    log.warn("Unknown command: {}", action);
            }
        } catch (Exception e) {
            log.error("Error processing command: {}", e.getMessage(), e);
        }
    }
}