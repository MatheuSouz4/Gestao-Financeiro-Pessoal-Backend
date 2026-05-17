package com.example.loginauthapi.repositories;

import com.example.loginauthapi.model.Conta;
import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    @Query("SELECT c FROM conta c WHERE " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:tipo IS NULL OR c.tipo = :tipo)")
    List<Conta> findByFiltros(@Param("status") Status status,
                              @Param("tipo") TipoConta tipo);
}