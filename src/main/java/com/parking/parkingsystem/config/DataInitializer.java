package com.parking.parkingsystem.config;

import com.parking.parkingsystem.model.ParkingSlot;
import com.parking.parkingsystem.model.User;
import com.parking.parkingsystem.repository.ParkingSlotRepository;
import com.parking.parkingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final ParkingSlotRepository slotRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {

            // Create Admin if not exists
            if (!userRepository.existsByEmail("admin@park.com")) {
                User admin = new User();
                admin.setFullName("Admin");
                admin.setEmail("admin@park.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(User.Role.ROLE_ADMIN);
                admin.setUserType(User.UserType.PERMANENT);
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println("✅ Admin created: admin@park.com / admin123");
            }

            // Seed sample parking slots
            if (slotRepository.count() == 0) {
                String[] zones = {"A", "B", "C", "D"};
                for (int z = 0; z < zones.length; z++) {
                    boolean isFourWheeler = (z % 2 == 0);
                    for (int i = 1; i <= 6; i++) {
                        ParkingSlot slot = new ParkingSlot();
                        slot.setSlotNumber(zones[z] + "-0" + i);
                        slot.setZone("Zone " + zones[z]);
                        slot.setVehicleType(isFourWheeler
                                ? ParkingSlot.VehicleType.FOUR_WHEELER
                                : ParkingSlot.VehicleType.TWO_WHEELER);
                        slot.setPricePerHour(isFourWheeler ? 40.0 : 25.0);
                        slot.setStatus(ParkingSlot.SlotStatus.AVAILABLE);
                        slot.setReservedForPermanentUser(i == 1); // slot 1 of each zone is reserved
                        slotRepository.save(slot);
                    }
                }
                System.out.println("✅ 24 sample parking slots seeded.");
            }
        };
    }
}
