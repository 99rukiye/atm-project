// src/main/java/com/atm/atmproject/controller/AdminController.java

package com.atm.atmproject.controller;

import com.atm.atmproject.dto.AdminRegisterRequestDTO;
import com.atm.atmproject.entity.Admin;
import com.atm.atmproject.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<Admin> register(@RequestBody @Valid AdminRegisterRequestDTO dto) {
        Admin admin = adminService.register(dto);
        return ResponseEntity.ok(admin);
    }
}
