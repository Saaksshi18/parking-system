package com.parking.parkingsystem.controller;

import com.parking.parkingsystem.model.*;
import com.parking.parkingsystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final ParkingSlotService slotService;
    private final BookingService bookingService;
    private final UserService userService;

    private User getCurrentUser(Authentication auth) {
        return userService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        model.addAttribute("user", user);
        model.addAttribute("availableSlots", slotService.countAvailable());
        model.addAttribute("myBookings", bookingService.getUserBookings(user).stream().limit(3).toList());
        return "user/dashboard";
    }

    @GetMapping("/slots")
    public String viewSlots(@RequestParam(required = false) String type, Model model) {
        if (type != null && !type.isEmpty()) {
            ParkingSlot.VehicleType vType = ParkingSlot.VehicleType.valueOf(type);
            model.addAttribute("slots", slotService.getAvailableSlotsByVehicleType(vType));
            model.addAttribute("selectedType", type);
        } else {
            model.addAttribute("slots", slotService.getAvailableSlots());
        }
        return "user/slots";
    }

    @GetMapping("/book/{slotId}")
    public String bookingForm(@PathVariable Long slotId, Model model) {
        ParkingSlot slot = slotService.getSlotById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));
        if (slot.getStatus() != ParkingSlot.SlotStatus.AVAILABLE) {
            return "redirect:/user/slots?error=unavailable";
        }
        model.addAttribute("slot", slot);
        return "user/book";
    }

    @PostMapping("/book/{slotId}")
    public String confirmBooking(@PathVariable Long slotId,
                                 @RequestParam int duration,
                                 @RequestParam String vehicleNumber,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = getCurrentUser(auth);
            ParkingSlot slot = slotService.getSlotById(slotId)
                    .orElseThrow(() -> new RuntimeException("Slot not found"));

            Booking booking = bookingService.createBooking(
                    user, slot, LocalDateTime.now(), duration, vehicleNumber);

            return "redirect:/user/booking/confirmation/" + booking.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/book/" + slotId;
        }
    }

    @GetMapping("/booking/confirmation/{id}")
    public String bookingConfirmation(@PathVariable Long id, Model model, Authentication auth) {
        Booking booking = bookingService.getBookingById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        model.addAttribute("booking", booking);
        return "user/confirmation";
    }

    @GetMapping("/bookings")
    public String myBookings(Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        model.addAttribute("bookings", bookingService.getUserBookings(user));
        return "user/bookings";
    }

    @GetMapping("/booking/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id, auth.getName());
            redirectAttributes.addFlashAttribute("message", "Booking cancelled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/bookings";
    }

    @GetMapping("/cost-preview")
    @ResponseBody
    public double costPreview(@RequestParam double pricePerHour, @RequestParam int hours) {
        return bookingService.calculateCost(pricePerHour, hours);
    }
}
