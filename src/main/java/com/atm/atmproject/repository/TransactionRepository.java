package com.atm.atmproject.repository;

import com.atm.atmproject.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Kullanıcının e-posta adresine göre işlemleri getir
    List<Transaction> findByUserEmail(String email);
}
