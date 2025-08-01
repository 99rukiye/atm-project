package com.atm.atmproject.service;

import com.atm.atmproject.dto.AdminRegisterRequestDTO;
import com.atm.atmproject.entity.Admin;

public interface AdminService {
    Admin register(AdminRegisterRequestDTO dto);
}
