package com.parking.parkingsystem.service;

import com.parking.parkingsystem.model.User;
import com.parking.parkingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.ROLE_USER);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void toggleUserStatus(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
        });
    }

    public long countUsers() {
        return userRepository.countByRole(User.Role.ROLE_USER);
    }
}
