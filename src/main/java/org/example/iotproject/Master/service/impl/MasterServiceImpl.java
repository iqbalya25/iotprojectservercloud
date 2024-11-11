package org.example.iotproject.Master.service.impl;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.msg.WriteCoilRequest;
import com.ghgande.j2mod.modbus.msg.WriteCoilResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.example.iotproject.Address.entity.Address;
import org.example.iotproject.Address.repository.AddressRepository;
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
    private TCPMasterConnection connection;
    private int slaveId;


    public MasterServiceImpl(AddressRepository addressRepository, MasterRepository masterRepository) {
        this.addressRepository = addressRepository;
        this.masterRepository = masterRepository;
    }

    @Override
    public void connectToMaster(String masterIpAddress) throws Exception {
        Optional<Master> plcMasterOpt = masterRepository.findByMasterIpAddress(masterIpAddress);
        if (plcMasterOpt.isEmpty()) {
            throw new IOException("PLC Master configuration not found for " + masterIpAddress);
        }

        Master master = plcMasterOpt.get();
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

    @Override
    public void turnOnBlower1() throws Exception {
        executeCommand("Blower_1_On");
    }

    @Override
    public void turnOffBlower1() throws Exception {
        executeCommand("Blower_1_Off");
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

    private ModbusResponse executeTransaction(WriteCoilRequest request) throws Exception {
        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();
        return transaction.getResponse();
    }
}


