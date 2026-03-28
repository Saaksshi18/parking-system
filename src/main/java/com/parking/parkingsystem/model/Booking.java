package com.parking.parkingsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private ParkingSlot parkingSlot;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private LocalDateTime actualEndTime;

    @Column(nullable = false)
    private Double baseCost;

    private Double penaltyAmount = 0.0;

    private Double totalAmount;

    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(columnDefinition = "LONGTEXT")
    private String qrCode;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum BookingStatus {
        ACTIVE, COMPLETED, CANCELLED
    }
}
