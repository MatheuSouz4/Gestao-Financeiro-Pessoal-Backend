package com.example.loginauthapi.repositories;

import com.example.loginauthapi.domain.cadastro.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, String> {
}