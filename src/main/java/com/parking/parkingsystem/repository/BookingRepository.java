package com.parking.parkingsystem.repository;

import com.parking.parkingsystem.model.Booking;
import com.parking.parkingsystem.model.ParkingSlot;
import com.parking.parkingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findByStatus(Booking.BookingStatus status);

    List<Booking> findByUserOrderByCreatedAtDesc(User user);

    // Check for conflicting bookings (prevent double booking)
    @Query("SELECT b FROM Booking b WHERE b.parkingSlot = :slot " +
           "AND b.status = 'ACTIVE' " +
           "AND ((:start < b.endTime) AND (:end > b.startTime))")
    List<Booking> findConflictingBookings(ParkingSlot slot,
                                          LocalDateTime start,
                                          LocalDateTime end);

    // Find active booking for a slot
    Optional<Booking> findByParkingSlotAndStatus(ParkingSlot slot,
                                                  Booking.BookingStatus status);

    // Find expired active bookings (for scheduler)
    @Query("SELECT b FROM Booking b WHERE b.status = 'ACTIVE' AND b.endTime < :now")
    List<Booking> findExpiredBookings(LocalDateTime now);

    // Revenue query
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = 'COMPLETED'")
    Double getTotalRevenue();

    // Today's revenue
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = 'COMPLETED' " +
           "AND b.createdAt >= :startOfDay")
    Double getTodayRevenue(LocalDateTime startOfDay);

    long countByStatus(Booking.BookingStatus status);
}
