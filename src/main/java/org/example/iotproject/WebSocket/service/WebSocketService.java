package org.example.iotproject.WebSocket.service;

import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.DeviceStatus.entity.DeviceStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendDeviceStatus(String status) {
        messagingTemplate.convertAndSend("/topic/device/status", status);
    }

    public void sendConnectionStatus(String status) {
        messagingTemplate.convertAndSend("/topic/connection/status", status);
    }
}