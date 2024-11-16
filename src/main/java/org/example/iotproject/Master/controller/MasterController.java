package org.example.iotproject.Master.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.iotproject.Master.entity.Master;
import org.example.iotproject.Master.repository.MasterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters")
@CrossOrigin(
        origins = {
                "https://iotproject-fe.vercel.app",
                "http://localhost:3000"
        },
        allowCredentials = "true"
)
public class MasterController {
    private final MasterRepository masterRepository;

    public MasterController(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }

    @GetMapping
    public ResponseEntity<List<Master>> getAllMasters() {
        List<Master> masters = masterRepository.findAll();
        return ResponseEntity.ok(masters);
    }
}

