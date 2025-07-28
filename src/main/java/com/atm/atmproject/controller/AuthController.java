package com.atm.atmproject.controller;

import com.atm.atmproject.dto.LoginRequestDTO;
import com.atm.atmproject.entity.User;
import com.atm.atmproject.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Başarılı giriş -> loginAttempt sıfırla
            User user = userRepository.findByEmail(request.getEmail()).get();
            user.setLoginAttempt(0);
            userRepository.save(user);

            return ResponseEntity.ok("Giriş başarılı");

        } catch (BadCredentialsException e) {
            // Hatalı giriş -> giriş hakkı azalt
            Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setLoginAttempt(user.getLoginAttempt() + 1);

                if (user.getLoginAttempt() >= 3) {
                    user.setLocked(true);
                }

                userRepository.save(user);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Hatalı giriş");
        }
    }
}
