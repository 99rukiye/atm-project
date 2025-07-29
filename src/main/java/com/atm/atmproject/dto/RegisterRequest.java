package com.atm.atmproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private Long adminId; // Hangi admin'e bağlı olduğu
}
