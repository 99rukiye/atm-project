package com.atm.atmproject.service.impl;

import com.atm.atmproject.dto.LoginRequestDTO;
import com.atm.atmproject.dto.UserRegisterRequestDTO;
import com.atm.atmproject.entity.User;
import com.atm.atmproject.repository.UserRepository;
import com.atm.atmproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(UserRegisterRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.warn("Kayıt başarısız - Email zaten kayıtlı: {}", dto.getEmail());
            throw new RuntimeException("Bu email zaten kayıtlı");
        }

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .balance(0.0)
                .loginAttempt(0)
                .locked(false)
                .build();

        userRepository.save(user);
        logger.info("Yeni kullanıcı kaydedildi - Email: {}", dto.getEmail());
        return user;
    }

    @Override
    public User login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Giriş başarısız - Kullanıcı bulunamadı: {}", dto.getEmail());
                    return new BadCredentialsException("Kullanıcı bulunamadı");
                });

        if (user.isLocked()) {
            logger.error("Hesap bloke - Email: {}", user.getEmail());
            throw new RuntimeException("Hesabınız bloke olmuştur.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            int attempts = user.getLoginAttempt() + 1;
            user.setLoginAttempt(attempts);

            if (attempts >= 3) {
                user.setLocked(true);
                logger.error("Hesap bloke edildi - Email: {}", user.getEmail());
            } else {
                logger.warn("Hatalı şifre - Email: {}, Deneme sayısı: {}", user.getEmail(), attempts);
            }

            userRepository.save(user);
            throw new BadCredentialsException("Hatalı şifre");
        }

        user.setLoginAttempt(0);
        userRepository.save(user);

        logger.info("Giriş başarılı - Email: {}", user.getEmail());
        return user;
    }

    @Override
    public User getUserById(Long id) {
        logger.info("getUserById çağrıldı - ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
    }

    @Override
    public void updateBalance(Long userId, Double amount) {
        logger.info("updateBalance çağrıldı - UserId: {}, Amount: {}", userId, amount);
        User user = getUserById(userId);
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
        logger.info("Yeni bakiye: {}", user.getBalance());
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));
    }

    @Override
    public void updateBalanceByEmail(String email, Double amount) {
        User user = getUserByEmail(email);
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
        logger.info("Para yatırıldı - Email: {}, Yeni bakiye: {}", email, user.getBalance());
    }

    @Override
    public boolean withdrawByEmail(String email, Double amount) {
        User user = getUserByEmail(email);
        if (user.getBalance() >= amount) {
            user.setBalance(user.getBalance() - amount);
            userRepository.save(user);
            logger.info("Para çekildi - Email: {}, Yeni bakiye: {}", email, user.getBalance());
            return true;
        } else {
            logger.warn("Yetersiz bakiye - Email: {}, Mevcut bakiye: {}", email, user.getBalance());
            return false;
        }
    }

    @Override
    public boolean transferMoney(String senderEmail, String receiverEmail, Double amount) {
        Optional<User> senderOpt = userRepository.findByEmail(senderEmail);
        Optional<User> receiverOpt = userRepository.findByEmail(receiverEmail);

        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            logger.error("Havale başarısız - Gönderici veya alıcı bulunamadı");
            return false;
        }

        User sender = senderOpt.get();
        User receiver = receiverOpt.get();

        if (sender.getBalance() >= amount) {
            sender.setBalance(sender.getBalance() - amount);
            receiver.setBalance(receiver.getBalance() + amount);

            userRepository.save(sender);
            userRepository.save(receiver);

            logger.info("Havale başarılı - Gönderen: {}, Alıcı: {}, Tutar: {}", senderEmail, receiverEmail, amount);
            return true;
        } else {
            logger.warn("Havale başarısız - Yetersiz bakiye: {}", sender.getBalance());
            return false;
        }
    }
}
