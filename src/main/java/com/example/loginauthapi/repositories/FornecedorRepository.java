
package com.example.loginauthapi.repositories;

import com.example.loginauthapi.domain.cadastro.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorRepository extends JpaRepository<Fornecedor, String> {
}