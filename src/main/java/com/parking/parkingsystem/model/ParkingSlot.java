package com.parking.parkingsystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "parking_slots")
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slotNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus status;

    @Column(nullable = false)
    private Double pricePerHour;

    private boolean reservedForPermanentUser;

    private String zone;

    public enum VehicleType {
        TWO_WHEELER, FOUR_WHEELER
    }

    public enum SlotStatus {
        AVAILABLE, OCCUPIED, RESERVED
    }
}
