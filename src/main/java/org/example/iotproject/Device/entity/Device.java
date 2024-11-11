package org.example.iotproject.Device.entity;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.*;
import lombok.Data;
import org.example.iotproject.Master.entity.Master;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@Data
@Entity
@Table(name = "device")
@SQLRestriction("deleted_at IS NULL")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_id_gen")
    @SequenceGenerator(name = "device_id_gen", sequenceName = "device_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToOne
    @JoinColumn(name = "master_id", nullable = false)
    private Master master;

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
