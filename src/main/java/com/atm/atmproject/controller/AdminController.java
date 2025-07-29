package com.atm.atmproject.controller;

import com.atm.atmproject.dto.UserSummaryDTO;
import com.atm.atmproject.entity.User;
import com.atm.atmproject.exception.NotFoundException;
import com.atm.atmproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<UserSummaryDTO> getAllUsers() {
        logger.info("Tüm kullanıcılar listeleniyor...");
        List<User> users = userRepository.findAll();
        logger.info("Toplam kullanıcı sayısı: {}", users.size());

        return users.stream()
                .map(user -> new UserSummaryDTO(user.getId(), user.getFullName(), user.getEmail(), user.getBalance()))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user-balance/{id}")
    public Double getUserBalance(@PathVariable Long id) {
        logger.info("Kullanıcının bakiyesi isteniyor - ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Kullanıcı bulunamadı - ID: {}", id);
                    return new NotFoundException("Kullanıcı bulunamadı");
                });

        logger.info("Kullanıcı bakiyesi: {}", user.getBalance());
        return user.getBalance();
    }
}
