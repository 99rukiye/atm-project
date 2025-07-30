package com.atm.atmproject.service;

import com.atm.atmproject.dto.RegisterRequest;
import com.atm.atmproject.entity.Admin;
import com.atm.atmproject.entity.User;
import com.atm.atmproject.entity.enums.Role;
import com.atm.atmproject.repository.AdminRepository;
import com.atm.atmproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request) {
        Optional<Admin> adminOptional = adminRepository.findById(request.getAdminId());
        if (adminOptional.isEmpty()) {
            return "Admin not found.";
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .admin(adminOptional.get())
                .balance(0.0)
                .loginAttempt(0)
                .locked(false)
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return "User registered successfully.";
    }

    public String login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isLocked()) {
            return "Account is locked.";
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            user.setLoginAttempt(user.getLoginAttempt() + 1);
            if (user.getLoginAttempt() >= 3) {
                user.setLocked(true);
            }
            userRepository.save(user);
            return "Incorrect password. Attempts left: " + (3 - user.getLoginAttempt());
        }

        user.setLoginAttempt(0); // sıfırla
        userRepository.save(user);

        return "Login successful.";
    }
}
