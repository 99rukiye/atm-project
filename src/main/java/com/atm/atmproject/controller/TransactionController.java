package com.atm.atmproject.controller;
import com.atm.atmproject.entity.Transaction;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.atm.atmproject.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    // Giriş yapan kullanıcının işlemlerini getirir
    @GetMapping("/my")
    public List<Transaction> getMyTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        logger.info("İşlem geçmişi talebi - Kullanıcı: {}", email);
        return transactionService.getTransactionsByEmail(email);
    }
}
