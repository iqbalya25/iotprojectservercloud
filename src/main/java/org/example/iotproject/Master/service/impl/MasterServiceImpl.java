package org.example.iotproject.Master.service.impl;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.example.iotproject.Address.entity.Address;
import org.example.iotproject.Address.repository.AddressRepository;
import org.example.iotproject.Device.entity.Device;
import org.example.iotproject.Device.service.DeviceService;
import org.example.iotproject.DeviceStatus.entity.DeviceStatus;
import org.example.iotproject.DeviceStatus.service.DeviceStatusService;
import org.example.iotproject.DeviceStatus.service.impl.DeviceStatusServiceImpl;
import org.example.iotproject.Master.entity.Master;
import org.example.iotproject.Master.repository.MasterRepository;
import org.example.iotproject.Master.service.MasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

@Service
public class MasterServiceImpl implements MasterService {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(MasterServiceImpl.class);
    private static final int PULSE_DURATION_MS = 100;
    private String connectionStatus = "disconnected";

    private final AddressRepository addressRepository;
    private final MasterRepository masterRepository;
    private final DeviceStatusService deviceStatusService;
    private final DeviceService deviceService;

    private TCPMasterConnection connection;
    private int slaveId;


    public MasterServiceImpl(AddressRepository addressRepository, MasterRepository masterRepository, DeviceStatusService deviceStatusService, DeviceService deviceService) {
        this.addressRepository = addressRepository;
        this.masterRepository = masterRepository;
        this.deviceStatusService = deviceStatusService;
        this.deviceService = deviceService;
    }

    @Override
    public void connectToMaster(String masterIpAddress) throws Exception {
        Optional<Master> plcMaster = masterRepository.findByMasterIpAddress(masterIpAddress);
        if (plcMaster.isEmpty()) {
            throw new Exception("PLC Master configuration not found for " + masterIpAddress);
        }

        Master master = plcMaster.get();
        InetAddress addr = InetAddress.getByName(master.getMasterIpAddress());
        this.slaveId = master.getPlcId();

        this.connection = new TCPMasterConnection(addr);
        connection.setPort(master.getMasterPort());

        if (!connection.isConnected()) {
            connection.connect();
            logger.info(master.getMasterIpAddress(), master.getMasterPort());
            connectionStatus = "Connected";
        } else {
            connectionStatus = "Connection Failed";
        }

    }

    @Override
    public void disconnectFromMaster() {
        if (connection != null && connection.isConnected()) {
            try {
                connection.close();
                connectionStatus = "Disconnected";
                logger.info("Successfully disconnected from master PLC.");
            } catch (Exception e) {
                logger.error("Failed to disconnect from master PLC.", e);
            }
        } else {
            logger.warn("Attempted to disconnect, but no active connection was found.");
        }
    }

    private void executeCommand(String addressName) throws Exception {
        if (connectionStatus == "Connection Failed" || connectionStatus == "Disconnected")   {
            throw new Exception("Connection Failed");
        }

        Optional<Address> addressOpt = addressRepository.findByAddressName(addressName);
        if (addressOpt.isEmpty()) {
            logger.error("Address {} not found in database", addressName);
            throw new IOException("Address not found in database");
        }

        int modbusAddress = addressOpt.get().getModbusAddress();
        sendMomentaryPulse(modbusAddress);
    }

    private Boolean getStatus(String addressName) throws Exception {
        if (connectionStatus == "Connection Failed" || connectionStatus == "Disconnected")   {
            throw new Exception("Connection Failed");
        }

        Optional<Address> addressOpt = addressRepository.findByAddressName(addressName);
        if (addressOpt.isEmpty()) {
            logger.error("Address {} not found in database", addressName);
            throw new IOException("Address not found in database");
        }

        int modbusAddress = addressOpt.get().getModbusAddress();
       return readCoil(modbusAddress);
    }


    @Override
    public void turnOnBlower1() throws Exception {
        executeCommand("Blower_1_On");
    }

    @Override
    public void turnOffBlower1() throws Exception {
        executeCommand("Blower_1_Off");
    }

    @Override
    public boolean getBlower1Status()  throws Exception{
        return getStatus("Blower_1_Status");
    }

    private void sendMomentaryPulse(int coil) throws IOException {
        try {
            writeCoil(coil, true);
            Thread.sleep(PULSE_DURATION_MS);
            writeCoil(coil, false);
            logger.info("Successfully sent momentary pulse to coil {}", coil);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while sending momentary pulse to coil {}", coil, e);
            throw new IOException("Failed to send momentary pulse", e);
        }
    }

    private void writeCoil(int coil, boolean state) throws IOException {
        try {
            if (!connection.isConnected()) {
                connection.connect();
            }
            WriteCoilRequest req = new WriteCoilRequest(coil, state);
            req.setUnitID(slaveId);
            ModbusResponse response = executeTransaction(req);
            if (!(response instanceof WriteCoilResponse)) {
                throw new IOException("Unexpected response type");
            }
            logger.info("Successfully wrote to coil {}: {}", coil, state);
        } catch (Exception e) {
            logger.error("Error writing to coil {}", coil, e);
            throw new IOException("Failed to write to coil", e);
        }
    }

    private boolean readCoil(int coil) throws IOException {
        try {
            if (!connection.isConnected()) {
                connection.connect();
            }
            ReadCoilsRequest req = new ReadCoilsRequest(coil, 1);
            req.setUnitID(slaveId);
            ModbusResponse response = executeTransaction(req);
            if (response instanceof ReadCoilsResponse) {
                ReadCoilsResponse readResponse = (ReadCoilsResponse) response;
                boolean state = readResponse.getCoilStatus(0);
                logger.info("Successfully read from coil {}: {}", coil, state);

                DeviceStatus deviceStatus = new DeviceStatus();
                Master master = masterRepository.findByPlcId(slaveId);
                deviceStatus.setMaster(master);

                Device device = deviceService.getDeviceByName("Blower_1");
                deviceStatus.setDevice(device);
                deviceStatus.setStatus(state);

                deviceStatusService.saveDeviceStatus(deviceStatus);
                return state;
            }

            throw new IOException("Unexpected response type");
        } catch (Exception e) {
            logger.error("Error reading from coil {}", coil, e);
            throw new IOException("Failed to read from coil", e);
        }
    }

    private ModbusResponse executeTransaction(ModbusRequest request) throws Exception {
        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();
        return transaction.getResponse();
    }
}


