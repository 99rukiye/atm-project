package com.atm.atmproject.security;

import com.atm.atmproject.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // email ile login olacak
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Süresi dolmamış hesap
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked(); // kilitli değilse true döner
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // şifre süresi dolmamış
    }

    @Override
    public boolean isEnabled() {
        return true; // aktif kullanıcı
    }
}
