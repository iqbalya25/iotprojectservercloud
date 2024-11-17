package org.example.iotproject.WebSocket.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.Mqtt.service.MqttService;
import org.example.iotproject.WebSocket.dto.CommandDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
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
    public void handleCommand(@Payload CommandDto command) {
        log.info("Received WebSocket command: {}", command);
        try {
            String action = command.getAction();
            log.info("Processing action: {}", action);

            Map<String, Object> mqttPayload = new HashMap<>();
            mqttPayload.put("action", action);

            // Handle Connection Commands
            if ("CONNECT_MASTER".equals(action) || "DISCONNECT_MASTER".equals(action)) {
                if ("CONNECT_MASTER".equals(action)) {
                    mqttPayload.put("ipAddress", command.getIpAddress());
                }
                log.info("Publishing connection command: {}", mqttPayload);
                mqttService.publish("plc/commands/connect", mqttPayload);
            }

            // Handle Blower Commands
            else if ("TURN_ON_BLOWER".equals(action) || "TURN_OFF_BLOWER".equals(action)) {
                log.info("Publishing blower command: {}", mqttPayload);
                mqttService.publish("plc/commands/blower", mqttPayload);
            }

            else {
                log.warn("Unknown command action: {}", action);
            }

        } catch (Exception e) {
            log.error("Error processing command: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/blower/frequency")
    public void handleBlowerFrequencyCommand(String commandJson) {
        try {
            JsonNode command = new ObjectMapper().readTree(commandJson);
            int frequency = command.get("frequency").asInt();

            mqttService.publish("plc/commands/blower/frequency",
                    Map.of("frequency", frequency));
            log.info("Published blower frequency command: {}", frequency);
        } catch (Exception e) {
            log.error("Error handling blower frequency command", e);
        }
    }
}