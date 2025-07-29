package com.atm.atmproject.service.impl;

import com.atm.atmproject.dto.LoginRequestDTO;
import com.atm.atmproject.dto.UserRegisterRequestDTO;
import com.atm.atmproject.entity.User;
import com.atm.atmproject.entity.enums.Role;
import com.atm.atmproject.repository.UserRepository;
import com.atm.atmproject.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.atm.atmproject.service.TransactionService;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionService transactionService;


    @Override
    public User register(UserRegisterRequestDTO dto) {
        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .balance(0.0)
                .loginAttempt(0)
                .locked(false)
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    @Override
    public User login(LoginRequestDTO dto) {
        return userRepository.findByEmail(dto.getEmail()).orElseThrow();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    @Override
    public void updateBalance(Long userId, Double amount) {
        User user = getUserById(userId);
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);

        transactionService.saveTransaction(
                "DEPOSIT",
                amount,
                "Para yatırma işlemi",
                user
        );
    }

    @Override
    public void updateBalanceByEmail(String email, Double amount) {
        User user = getUserByEmail(email);
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);

        transactionService.saveTransaction(
                "DEPOSIT",
                amount,
                "E-posta ile para yatırma",
                user
        );
    }

    @Override
    public boolean withdrawByEmail(String email, Double amount) {
        User user = getUserByEmail(email);
        if (user.getBalance() >= amount) {
            user.setBalance(user.getBalance() - amount);
            userRepository.save(user);

            transactionService.saveTransaction(
                    "WITHDRAW",
                    amount,
                    "Para çekme işlemi",
                    user
            );

            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean transferMoney(String senderEmail, String receiverEmail, Double amount) {
        User sender = getUserByEmail(senderEmail);
        User receiver = getUserByEmail(receiverEmail);

        if (sender.getBalance() >= amount) {
            sender.setBalance(sender.getBalance() - amount);
            receiver.setBalance(receiver.getBalance() + amount);
            userRepository.save(sender);
            userRepository.save(receiver);

            transactionService.saveTransaction(
                    "TRANSFER",
                    amount,
                    "Havale - Gönderen: " + senderEmail + ", Alıcı: " + receiverEmail,
                    sender
            );

            return true;
        }
        return false;
    }
}
