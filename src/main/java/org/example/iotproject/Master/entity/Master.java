package org.example.iotproject.Master.entity;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@Data
@Entity
@Table(name = "master")
@SQLRestriction("deleted_at IS NULL")
public class Master {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "master_id_gen")
    @SequenceGenerator(name = "master_id_gen", sequenceName = "master_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "master_name")  // This keeps the database column name as master_name
    private String masterName;

    @Column(name = "master_ip_address")
    private String masterIpAddress;

    @Column(name = "master_port")
    private int masterPort;

    @Column(name = "plc_id")
    private int plcId;

    @Column(name = "master_location")
    private String masterLocation;

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
