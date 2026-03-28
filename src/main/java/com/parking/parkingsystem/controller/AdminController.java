package com.parking.parkingsystem.controller;

import com.parking.parkingsystem.model.ParkingSlot;
import com.parking.parkingsystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ParkingSlotService slotService;
    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalSlots", slotService.countTotal());
        model.addAttribute("availableSlots", slotService.countAvailable());
        model.addAttribute("occupiedSlots", slotService.countOccupied());
        model.addAttribute("activeBookings", bookingService.countActiveBookings());
        model.addAttribute("todayRevenue", bookingService.getTodayRevenue());
        model.addAttribute("totalRevenue", bookingService.getTotalRevenue());
        model.addAttribute("recentBookings", bookingService.getAllBookings()
                .stream().limit(5).toList());
        model.addAttribute("totalUsers", userService.countUsers());
        return "admin/dashboard";
    }

    // ── Slots ──────────────────────────────────────────────
    @GetMapping("/slots")
    public String slots(Model model) {
        model.addAttribute("slots", slotService.getAllSlots());
        model.addAttribute("newSlot", new ParkingSlot());
        return "admin/slots";
    }

    @PostMapping("/slots/add")
    public String addSlot(@ModelAttribute ParkingSlot slot,
                          RedirectAttributes redirectAttributes) {
        if (slotService.slotNumberExists(slot.getSlotNumber())) {
            redirectAttributes.addFlashAttribute("error",
                    "Slot number " + slot.getSlotNumber() + " already exists.");
            return "redirect:/admin/slots";
        }
        slotService.saveSlot(slot);
        redirectAttributes.addFlashAttribute("message", "Slot added successfully.");
        return "redirect:/admin/slots";
    }

    @GetMapping("/slots/edit/{id}")
    public String editSlotForm(@PathVariable Long id, Model model) {
        model.addAttribute("slot", slotService.getSlotById(id)
                .orElseThrow(() -> new RuntimeException("Slot not found")));
        return "admin/edit-slot";
    }

    @PostMapping("/slots/edit/{id}")
    public String editSlot(@PathVariable Long id, @ModelAttribute ParkingSlot slot,
                           RedirectAttributes redirectAttributes) {
        slot.setId(id);
        slotService.saveSlot(slot);
        redirectAttributes.addFlashAttribute("message", "Slot updated successfully.");
        return "redirect:/admin/slots";
    }

    @GetMapping("/slots/delete/{id}")
    public String deleteSlot(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        slotService.deleteSlot(id);
        redirectAttributes.addFlashAttribute("message", "Slot deleted.");
        return "redirect:/admin/slots";
    }

    // ── Bookings ──────────────────────────────────────────────
    @GetMapping("/bookings")
    public String bookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "admin/bookings";
    }

    @GetMapping("/bookings/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.getBookingById(id).ifPresent(b -> {
                b.setStatus(com.parking.parkingsystem.model.Booking.BookingStatus.CANCELLED);
                b.getParkingSlot().setStatus(ParkingSlot.SlotStatus.AVAILABLE);
            });
            redirectAttributes.addFlashAttribute("message", "Booking cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    // ── Users ──────────────────────────────────────────────
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute("message", "User status updated.");
        return "redirect:/admin/users";
    }

    // ── Revenue ──────────────────────────────────────────────
    @GetMapping("/revenue")
    public String revenue(Model model) {
        model.addAttribute("totalRevenue", bookingService.getTotalRevenue());
        model.addAttribute("todayRevenue", bookingService.getTodayRevenue());
        model.addAttribute("allBookings", bookingService.getAllBookings());
        return "admin/revenue";
    }
}
