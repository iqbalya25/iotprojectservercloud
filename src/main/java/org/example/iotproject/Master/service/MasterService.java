package org.example.iotproject.Master.service;

import java.io.IOException;

public interface MasterService {
    void turnOnBlower1() throws Exception;
    void turnOffBlower1() throws Exception;
   void connectToMaster(String masterName) throws Exception;
}
