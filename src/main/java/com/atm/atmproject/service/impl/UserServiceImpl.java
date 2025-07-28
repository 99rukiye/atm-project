// src/main/java/com/atm/atmproject/service/impl/UserServiceImpl.java

package com.atm.atmproject.service.impl;

import com.atm.atmproject.dto.LoginRequestDTO;
import com.atm.atmproject.dto.UserRegisterRequestDTO;
import com.atm.atmproject.entity.User;
import com.atm.atmproject.entity.enums.Role;
import com.atm.atmproject.repository.UserRepository;
import com.atm.atmproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(UserRegisterRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Bu e-posta ile zaten kayıt olunmuş.");
        }

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
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        if (user.isLocked()) {
            throw new RuntimeException("Hesabınız bloke edilmiştir.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            int attempt = user.getLoginAttempt() + 1;
            user.setLoginAttempt(attempt);
            if (attempt >= 3) {
                user.setLocked(true);
            }
            userRepository.save(user);
            throw new RuntimeException("Hatalı şifre. Kalan hakkınız: " + (3 - attempt));
        }

        user.setLoginAttempt(0);
        return userRepository.save(user);
    }
}
