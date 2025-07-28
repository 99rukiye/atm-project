package com.atm.atmproject.controller;

import com.atm.atmproject.dto.LoginRequestDTO;
import com.atm.atmproject.dto.UserRegisterRequestDTO;
import com.atm.atmproject.entity.User;
import com.atm.atmproject.repository.UserRepository;
import com.atm.atmproject.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDTO request) {
        logger.info("Login attempt for email: {}", request.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            user.setLoginAttempt(0);
            userRepository.save(user);

            logger.info("Login successful for user: {}", user.getEmail());
            return ResponseEntity.ok("Giriş başarılı");

        } catch (BadCredentialsException e) {
            logger.warn("Bad credentials for email: {}", request.getEmail());

            Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setLoginAttempt(user.getLoginAttempt() + 1);

                if (user.getLoginAttempt() >= 3) {
                    user.setLocked(true);
                    logger.error("User account locked: {}", user.getEmail());
                }

                userRepository.save(user);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Hatalı giriş");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRegisterRequestDTO dto) {
        try {
            userService.register(dto);
            logger.info("Yeni kullanıcı kaydedildi: {}", dto.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body("Kayıt başarılı");
        } catch (Exception e) {
            logger.error("Kayıt sırasında hata oluştu: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kayıt başarısız: " + e.getMessage());
        }
    }
}
