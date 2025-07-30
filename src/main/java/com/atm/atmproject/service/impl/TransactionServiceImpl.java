package com.atm.atmproject.service.impl;

import com.atm.atmproject.entity.Transaction;
import com.atm.atmproject.entity.User;
import com.atm.atmproject.repository.TransactionRepository;
import com.atm.atmproject.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(String type, Double amount, String description, User user) {
        Transaction transaction = Transaction.builder()
                .type(type)
                .amount(amount)
                .description(description)
                .timestamp(LocalDateTime.now())
                .user(user)
                .build();

        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionsByEmail(String email) {
        return transactionRepository.findByUserEmail(email);
    }
}
