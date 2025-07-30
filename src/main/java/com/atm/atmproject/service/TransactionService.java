package com.atm.atmproject.service;

import com.atm.atmproject.entity.Transaction;
import com.atm.atmproject.entity.User;

import java.util.List;

public interface TransactionService {

    void saveTransaction(String type, Double amount, String description, User user);

    List<Transaction> getTransactionsByEmail(String email);
}
