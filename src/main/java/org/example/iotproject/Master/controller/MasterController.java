package org.example.iotproject.Master.controller;

import org.example.iotproject.Master.dto.CommandRequestDTO;
import org.example.iotproject.Master.dto.ConnectRequestDTO;
import org.example.iotproject.Master.dto.StatusRequestDTO;
import org.example.iotproject.Master.service.MasterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/iotproject")
public class MasterController {
    private final MasterService masterService;

    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

//    @PostMapping("/Blower1")
//    public ResponseEntity<String> executeCommand(@RequestBody CommandRequestDTO command) throws Exception {
//        try {
//            if ("ON".equalsIgnoreCase(command.getCommand())) {
//                masterService.turnOnBlower1();
//                return ResponseEntity.ok("Blower 1 On");
//            } else if ("OFF".equalsIgnoreCase(command.getCommand())) {
//                masterService.turnOffBlower1();
//                return ResponseEntity.ok("Blower 1 Off");
//            } else {
//                return ResponseEntity.badRequest().body("Invalid command. Use 'ON' or 'OFF'.");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @PostMapping("/connect")
//    public ResponseEntity<String> connectToMaster(@RequestBody ConnectRequestDTO connect) throws Exception {
//        masterService.connectToMaster(connect.getMasterIPaddress());
//        return ResponseEntity.ok("Master Connected");
//    }
//
//    @PostMapping("/disconnect")
//    public ResponseEntity<String> disconnectFromMaster() {
//        masterService.disconnectFromMaster();
//        return ResponseEntity.ok("Disconnected from Master PLC.");
//    }
//
//    @GetMapping("/Blower1")
//    public ResponseEntity<StatusRequestDTO> getBlower1Status() throws Exception {
//        Boolean Status = masterService.getBlower1Status();
//        StatusRequestDTO statusRequestDTO = new StatusRequestDTO();
//
//        if (Status == true) {
//            statusRequestDTO.setBlower1Status("Blower 1 On");
//        } else {
//            statusRequestDTO.setBlower1Status("Blower 1 Off");
//        }
//        return ResponseEntity.ok(statusRequestDTO);
//    }
}
