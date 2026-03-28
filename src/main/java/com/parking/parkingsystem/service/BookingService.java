package com.parking.parkingsystem.service;

import com.parking.parkingsystem.model.*;
import com.parking.parkingsystem.repository.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    // Overstay penalty rate per hour
    private static final double PENALTY_RATE_PER_HOUR = 20.0;

    @Transactional
    public Booking createBooking(User user, ParkingSlot slot,
                                 LocalDateTime startTime, int durationHours,
                                 String vehicleNumber) throws Exception {

        LocalDateTime endTime = startTime.plusHours(durationHours);

        // 1. Check for conflicting bookings (prevent double booking)
        List<Booking> conflicts = bookingRepository.findConflictingBookings(slot, startTime, endTime);
        if (!conflicts.isEmpty()) {
            throw new Exception("Slot " + slot.getSlotNumber() + " is already booked for this time.");
        }

        // 2. Calculate cost
        double baseCost = slot.getPricePerHour() * durationHours;
        double totalAmount = baseCost * 1.05; // 5% tax

        // 3. Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setParkingSlot(slot);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setVehicleNumber(vehicleNumber);
        booking.setBaseCost(baseCost);
        booking.setPenaltyAmount(0.0);
        booking.setTotalAmount(totalAmount);
        booking.setStatus(Booking.BookingStatus.ACTIVE);

        // 4. Mark slot as OCCUPIED
        slot.setStatus(ParkingSlot.SlotStatus.OCCUPIED);
        parkingSlotRepository.save(slot);

        // 5. Generate QR code
        String qrData = "BOOKING:" + booking.getId() + "|SLOT:" + slot.getSlotNumber()
                + "|USER:" + user.getEmail() + "|START:" + startTime;
        booking.setQrCode(generateQRCodeBase64(qrData));

        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, String userEmail) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new Exception("Booking not found"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new Exception("Unauthorized to cancel this booking");
        }

        if (booking.getStatus() != Booking.BookingStatus.ACTIVE) {
            throw new Exception("Only active bookings can be cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.getParkingSlot().setStatus(ParkingSlot.SlotStatus.AVAILABLE);
        parkingSlotRepository.save(booking.getParkingSlot());
        bookingRepository.save(booking);
    }

    // Calculate cost preview before booking
    public double calculateCost(double pricePerHour, int hours) {
        double base = pricePerHour * hours;
        return base * 1.05;
    }

    // Calculate overstay penalty
    public double calculatePenalty(Booking booking) {
        if (booking.getActualEndTime() == null) return 0.0;
        long extraMinutes = ChronoUnit.MINUTES.between(booking.getEndTime(), booking.getActualEndTime());
        if (extraMinutes <= 0) return 0.0;
        double extraHours = Math.ceil(extraMinutes / 60.0);
        return extraHours * PENALTY_RATE_PER_HOUR;
    }

    // Auto-release expired slots every 5 minutes
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void releaseExpiredBookings() {
        List<Booking> expired = bookingRepository.findExpiredBookings(LocalDateTime.now());
        for (Booking booking : expired) {
            double penalty = calculatePenalty(booking);
            booking.setPenaltyAmount(penalty);
            booking.setTotalAmount(booking.getTotalAmount() + penalty);
            booking.setStatus(Booking.BookingStatus.COMPLETED);
            booking.getParkingSlot().setStatus(ParkingSlot.SlotStatus.AVAILABLE);
            parkingSlotRepository.save(booking.getParkingSlot());
            bookingRepository.save(booking);
        }
    }

    public List<Booking> getUserBookings(User user) {
        return bookingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getActiveBookings() {
        return bookingRepository.findByStatus(Booking.BookingStatus.ACTIVE);
    }

    public long countActiveBookings() {
        return bookingRepository.countByStatus(Booking.BookingStatus.ACTIVE);
    }

    public Double getTotalRevenue() {
        Double rev = bookingRepository.getTotalRevenue();
        return rev != null ? rev : 0.0;
    }

    public Double getTodayRevenue() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        Double rev = bookingRepository.getTodayRevenue(startOfDay);
        return rev != null ? rev : 0.0;
    }

    // Generate QR Code as Base64 string
    private String generateQRCodeBase64(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }
}
