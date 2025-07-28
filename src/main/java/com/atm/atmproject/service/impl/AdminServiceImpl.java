// src/main/java/com/atm/atmproject/service/impl/AdminServiceImpl.java

package com.atm.atmproject.service.impl;

import com.atm.atmproject.dto.AdminRegisterRequestDTO;
import com.atm.atmproject.entity.Admin;
import com.atm.atmproject.repository.AdminRepository;
import com.atm.atmproject.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Admin register(AdminRegisterRequestDTO dto) {
        if (adminRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Bu e-posta ile zaten bir admin var.");
        }

        Admin admin = Admin.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        return adminRepository.save(admin);
    }
}

