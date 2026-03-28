package com.parking.parkingsystem.service;

import com.parking.parkingsystem.model.ParkingSlot;
import com.parking.parkingsystem.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParkingSlotService {

    private final ParkingSlotRepository parkingSlotRepository;

    public List<ParkingSlot> getAllSlots() {
        return parkingSlotRepository.findAll();
    }

    public List<ParkingSlot> getAvailableSlots() {
        return parkingSlotRepository.findByStatus(ParkingSlot.SlotStatus.AVAILABLE);
    }

    public List<ParkingSlot> getSlotsByVehicleType(ParkingSlot.VehicleType type) {
        return parkingSlotRepository.findByVehicleType(type);
    }

    public List<ParkingSlot> getAvailableSlotsByVehicleType(ParkingSlot.VehicleType type) {
        return parkingSlotRepository.findByVehicleTypeAndStatus(type, ParkingSlot.SlotStatus.AVAILABLE);
    }

    public Optional<ParkingSlot> getSlotById(Long id) {
        return parkingSlotRepository.findById(id);
    }

    public ParkingSlot saveSlot(ParkingSlot slot) {
        if (slot.getStatus() == null) {
            slot.setStatus(ParkingSlot.SlotStatus.AVAILABLE);
        }
        return parkingSlotRepository.save(slot);
    }

    public void deleteSlot(Long id) {
        parkingSlotRepository.deleteById(id);
    }

    public boolean slotNumberExists(String slotNumber) {
        return parkingSlotRepository.existsBySlotNumber(slotNumber);
    }

    public long countAvailable() {
        return parkingSlotRepository.countByStatus(ParkingSlot.SlotStatus.AVAILABLE);
    }

    public long countOccupied() {
        return parkingSlotRepository.countByStatus(ParkingSlot.SlotStatus.OCCUPIED);
    }

    public long countTotal() {
        return parkingSlotRepository.count();
    }
}
