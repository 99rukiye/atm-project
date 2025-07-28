package com.atm.atmproject.entity;

import com.atm.atmproject.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @ManyToOne
    @JoinColumn(name = "admin_id") // foreign key olacak sütun
    private Admin admin;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private Double balance;

    private int loginAttempt;

    private boolean locked;

    @Enumerated(EnumType.STRING)
    private Role role;
// src/main/java/com/atm/atmproject/entity/User.java



}
