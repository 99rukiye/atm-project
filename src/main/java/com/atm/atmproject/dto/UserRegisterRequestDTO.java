package com.atm.atmproject.dto;

import com.atm.atmproject.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterRequestDTO {

    @NotBlank(message = "Ad soyad boş olamaz")
    private String fullName;

    @Email(message = "Geçerli bir email adresi giriniz")
    @NotBlank(message = "Email boş olamaz")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    private String password;

    private Role role;
}
