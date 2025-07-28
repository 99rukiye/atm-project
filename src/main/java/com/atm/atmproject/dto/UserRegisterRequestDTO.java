package com.atm.atmproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterRequestDTO {

    @NotBlank
    private String fullName;

    @Email
    private String email;

    @NotBlank
    private String password;
}
