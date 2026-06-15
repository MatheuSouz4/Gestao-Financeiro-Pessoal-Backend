package com.example.loginauthapi.repositories;

import com.example.loginauthapi.model.Conta;
import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    @Query("SELECT c FROM Conta c WHERE " + // Ajustado de 'conta' para 'Conta' (Maiúscula)
            "(:status IS NULL OR c.status = :status) AND " +
            "(:tipo IS NULL OR c.tipo = :tipo)")
    List<Conta> findByFiltros(@Param("status") Status status,
                              @Param("tipo") TipoConta tipo);

    Optional<Conta> findByNomeIgnoreCaseAndTipoAndClienteId(String nome, TipoConta tipo, Long clienteId);

    Optional<Conta> findByNomeIgnoreCaseAndTipoAndFornecedorId(String nome, TipoConta tipo, Long fornecedorId);

    @Query("SELECT COALESCE(SUM(CASE WHEN c.tipo = 'RECEITA' THEN c.saldoAtual ELSE -c.saldoAtual END), 0) " +
            "FROM Conta c WHERE (:contaId IS NULL OR c.id = :contaId)")
    BigDecimal obterSaldoConsolidadoSemFiltroData(@Param("contaId") Long contaId);

    @Query("SELECT COALESCE(SUM(CASE WHEN f.conta.tipo = 'RECEITA' THEN f.valorPago ELSE -f.valorPago END), 0) " +
            "FROM Financeiro f " +
            "WHERE (:contaId IS NULL OR f.conta.id = :contaId) " +
            "AND f.status IN ('PAGA', 'PAGAMENTO_PARCIAL') " +
            "AND (:inicio IS NULL OR f.dataPagamento >= :inicio) " +
            "AND (:fim IS NULL OR f.dataPagamento <= :fim)")
    BigDecimal obterSaldoConsolidadoComFiltro(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("contaId") Long contaId
    );
}