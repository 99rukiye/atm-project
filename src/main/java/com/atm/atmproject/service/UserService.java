package com.atm.atmproject.service;

import com.atm.atmproject.dto.LoginRequestDTO;
import com.atm.atmproject.dto.UserRegisterRequestDTO;
import com.atm.atmproject.entity.User;

public interface UserService {
    User register(UserRegisterRequestDTO dto);
    User login(LoginRequestDTO dto);
}
