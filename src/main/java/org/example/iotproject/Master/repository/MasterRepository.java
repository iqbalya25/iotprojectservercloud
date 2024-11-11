package org.example.iotproject.Master.repository;

import org.example.iotproject.Master.entity.Master;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterRepository extends JpaRepository<Master, Long> {
    Optional<Master> findByMasterName (String masterName);
    Optional<Master> findByMasterIpAddress (String masterIpAddress);
}
