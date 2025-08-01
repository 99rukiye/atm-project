package com.atm.atmproject.service;

import com.atm.atmproject.dto.LoginRequestDTO;
import com.atm.atmproject.dto.UserRegisterRequestDTO;
import com.atm.atmproject.entity.User;

public interface UserService {
    User register(UserRegisterRequestDTO dto);
    User login(LoginRequestDTO dto);

    User getUserById(Long id);
    User getUserByEmail(String email);

    void updateBalance(Long userId, Double amount);
    void updateBalanceByEmail(String email, Double amount);

    boolean withdrawByEmail(String email, Double amount);
    boolean transferMoney(String senderEmail, String receiverEmail, Double amount);
}
