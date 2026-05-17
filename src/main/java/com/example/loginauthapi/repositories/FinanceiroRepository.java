package com.example.loginauthapi.repositories;

import com.example.loginauthapi.model.Financeiro;
import com.example.loginauthapi.model.StatusLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // IMPORTANTE
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinanceiroRepository extends JpaRepository<Financeiro, Long>, JpaSpecificationExecutor<Financeiro> {
    List<Financeiro> findByStatusInAndDataVencimentoBefore(List<StatusLancamento> statuses, LocalDate data);
}