package org.example.iotproject.WebSocket.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.Mqtt.service.MqttService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@Slf4j
public class WebSocketController {
    private final MqttService mqttService;

    public WebSocketController(MqttService mqttService) {
        this.mqttService = mqttService;
    }

    @MessageMapping("/device/command")
    public void handleCommand(String commandJson) {
        log.info("Received WebSocket command: {}", commandJson);

        try {
            JsonNode command = new ObjectMapper().readTree(commandJson);
            String action = command.get("action").asText();
            log.info("Processing action: {}", action);

            switch (action) {
                case "CONNECT_MASTER":
                    String ipAddress = command.get("ipAddress").asText();
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
            log.error("Error handling command: {}", commandJson, e);
        }
    }
}