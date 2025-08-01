package com.atm.atmproject.controller;

import com.atm.atmproject.entity.User;
import com.atm.atmproject.repository.UserRepository;
import com.atm.atmproject.service.UserService;

import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    private final UserService userService;

    // 1. Giriş yapan kullanıcı bilgisi
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("getCurrentUser called by {}", userDetails.getUsername());
        User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> request) {
        try {
            String email = (String) request.get("email");
            Double amount = Double.parseDouble(request.get("amount").toString());

            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                logger.warn("Kullanıcı bulunamadı: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kullanıcı bulunamadı");
            }

            User user = optionalUser.get();
            Double currentBalance = user.getBalance() != null ? user.getBalance() : 0.0;
            user.setBalance(currentBalance + amount);
            userRepository.save(user);

            logger.info("{} kullanıcısı {} TL yatırdı.", email, amount);
            return ResponseEntity.ok(amount + " TL başarıyla yatırıldı.");

        } catch (Exception e) {
            logger.error("Para yatırma hatası: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Bir hata oluştu: " + e.getMessage());
        }
    }

    // 3. Para çekme
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestParam Double amount) {
        logger.info("Withdraw request - User: {}, Amount: {}", userDetails.getUsername(), amount);
        boolean success = userService.withdrawByEmail(userDetails.getUsername(), amount);
        if (success) {
            return ResponseEntity.ok("Para çekme başarılı.");
        } else {
            return ResponseEntity.badRequest().body("Yetersiz bakiye.");
        }
    }

    // 4. Havale
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@AuthenticationPrincipal UserDetails senderDetails,
                                           @RequestParam @Email String receiverEmail,
                                           @RequestParam Double amount) {
        logger.info("Transfer request - From: {}, To: {}, Amount: {}", senderDetails.getUsername(), receiverEmail, amount);
        boolean success = userService.transferMoney(senderDetails.getUsername(), receiverEmail, amount);
        if (success) {
            return ResponseEntity.ok("Havale başarılı.");
        } else {
            return ResponseEntity.badRequest().body("Havale başarısız. Yetersiz bakiye veya kullanıcı bulunamadı.");
        }
    }
}
