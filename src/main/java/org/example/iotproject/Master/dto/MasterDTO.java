package org.example.iotproject.Master.dto;

import lombok.Data;

@Data
public class MasterDTO {
    private Long id;
    private String masterName;
    private String masterIpAddress;
    private int masterPort;
    private int plcId;
    private String masterLocation;
}