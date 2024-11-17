package org.example.iotproject.WebSocket.dto;

import lombok.Data;

@Data
public class CommandDto {
    private String action;
    private String ipAddress;
}

///