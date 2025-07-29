package com.atm.atmproject.controller;

import com.atm.atmproject.entity.User;
import com.atm.atmproject.service.UserService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    // 1. Giriş yapan kullanıcı bilgisi
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("getCurrentUser called by {}", userDetails.getUsername());
        User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    // 2. Para yatırma
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam Double amount) {
        logger.info("Deposit request - User: {}, Amount: {}", userDetails.getUsername(), amount);
        userService.updateBalanceByEmail(userDetails.getUsername(), amount);
        return ResponseEntity.ok("Para yatırma başarılı.");
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
