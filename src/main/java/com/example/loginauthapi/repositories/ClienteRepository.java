
package com.example.loginauthapi.repositories;

import com.example.loginauthapi.domain.cadastro.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, String> {
}