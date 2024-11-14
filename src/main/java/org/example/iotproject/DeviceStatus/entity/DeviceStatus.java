package org.example.iotproject.DeviceStatus.entity;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.*;
import lombok.Data;
import org.example.iotproject.Device.entity.Device;
import org.example.iotproject.Master.entity.Master;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@Data
@Entity
@Table(name = "device_status")
@SQLRestriction("deleted_at IS NULL")
public class DeviceStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_status_id_gen")
    @SequenceGenerator(name = "device_status_id_gen", sequenceName = "device_status_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "master_id", nullable = false)
    private Master master;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    void onSave() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @PreDestroy
    void onDelete() {
        this.deletedAt = Instant.now();
    }
}