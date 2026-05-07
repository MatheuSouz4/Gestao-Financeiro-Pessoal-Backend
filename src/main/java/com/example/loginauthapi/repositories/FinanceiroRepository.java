package com.example.loginauthapi.repositories;

import com.example.loginauthapi.model.Financeiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceiroRepository extends JpaRepository<Financeiro, Long>, JpaSpecificationExecutor<Financeiro>{
}