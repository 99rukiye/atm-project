package com.atm.atmproject.controller;

import com.atm.atmproject.entity.Transaction;
import com.atm.atmproject.entity.User;
import com.atm.atmproject.repository.UserRepository;
import com.atm.atmproject.service.TransactionService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    // 1. Giriş yapan kullanıcının işlemlerini getirir
    @GetMapping("/my")
    public List<Transaction> getMyTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        logger.info("İşlem geçmişi talebi - Kullanıcı: {}", email);
        return transactionService.getTransactionsByEmail(email);
    }

    // 2. Para yatırma işlemi
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
            user.setBalance(user.getBalance() + amount);
            userRepository.save(user);

            logger.info("{} kullanıcısı {} TL yatırdı.", email, amount);
            return ResponseEntity.ok(amount + " TL başarıyla yatırıldı.");

        } catch (Exception e) {
            logger.error("Para yatırma hatası: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Bir hata oluştu: " + e.getMessage());
        }
    }
}
