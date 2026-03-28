package com.parking.parkingsystem.repository;

import com.parking.parkingsystem.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    List<ParkingSlot> findByStatus(ParkingSlot.SlotStatus status);

    List<ParkingSlot> findByVehicleType(ParkingSlot.VehicleType vehicleType);

    List<ParkingSlot> findByVehicleTypeAndStatus(ParkingSlot.VehicleType vehicleType,
                                                  ParkingSlot.SlotStatus status);

    List<ParkingSlot> findByZone(String zone);

    boolean existsBySlotNumber(String slotNumber);

    long countByStatus(ParkingSlot.SlotStatus status);
}
