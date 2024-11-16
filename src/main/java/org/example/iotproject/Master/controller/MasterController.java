package org.example.iotproject.Master.controller;

import org.example.iotproject.Master.entity.Master;
import org.example.iotproject.Master.repository.MasterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/masters")
@CrossOrigin(origins = "http://localhost:3000")
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

    @GetMapping("/{id}")
    public ResponseEntity<Master> getMasterById(@PathVariable Long id) {
        return masterRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}


