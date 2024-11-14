package org.example.iotproject.Master.service;

import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

public interface MasterService {
    void disconnectFromMaster();

    void turnOnBlower1() throws Exception;

    void turnOffBlower1() throws Exception;
    void connectToMaster(String masterName) throws Exception;

    boolean getBlower1Status()  throws Exception;
}
