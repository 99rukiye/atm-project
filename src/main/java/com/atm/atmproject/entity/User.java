package com.atm.atmproject.entity;

import com.atm.atmproject.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User  {

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    public String getUsername() {
        return this.email;
    }



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(nullable = false)
    private Double balance = 0.0;
    @Column(name = "login_attempt")
    private int loginAttempt = 0;

    @Column(name = "is_blocked")
    private boolean isBlocked = false;


  

    private boolean locked;

    @Enumerated(EnumType.STRING)
    private Role role;

}
