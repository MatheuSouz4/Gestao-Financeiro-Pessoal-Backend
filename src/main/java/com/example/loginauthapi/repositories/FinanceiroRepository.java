package com.example.loginauthapi.repositories;

import com.example.loginauthapi.dto.DashboardProjection;
import com.example.loginauthapi.model.Financeiro;
import com.example.loginauthapi.model.StatusLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinanceiroRepository extends JpaRepository<Financeiro, Long>, JpaSpecificationExecutor<Financeiro> {

    // Utilizado pelo FinanceiroService para filtrar lançamentos pendentes vencidos
    List<Financeiro> findByStatusInAndDataVencimentoBefore(List<StatusLancamento> statuses, LocalDate data);

    // Utilizado pelo fluxo de estorno (expurgo de resíduos de parcelas)
    List<Financeiro> findByIdReferencia(Long idReferencia);

    /**
     * MANTIDA AQUI: Esta query é uma exceção necessária, pois é usada internamente
     * no fluxo de ESCREVER/ATUALIZAR o saldo na tabela 'conta'.
     * Não é uma query de relatório, mas sim de cálculo de estado de conta.
     */
    @Query("""
        SELECT COALESCE(SUM(CASE WHEN f.conta.tipo = 'RECEITA' THEN f.valor ELSE -f.valor END), 0)
        FROM Financeiro f
        WHERE f.status = 'PAGA'
          AND f.dataPagamento <= :hoje
          AND (:contaId IS NULL OR f.conta.id = :contaId)
        """)
    BigDecimal obterSaldoConsolidadoAteHoje(
            @Param("hoje") LocalDate hoje,
            @Param("contaId") Long contaId
    );

    // Adicione este método dentro do seu FinanceiroRepository existente

    @Query("""
    SELECT 
        SUM(CASE WHEN c.tipo = 'RECEITA' AND f.status IN ('PAGA', 'PAGAMENTO_PARCIAL') THEN COALESCE(f.valorPago, f.valor) ELSE 0 END) AS receitasRecebidas,
        SUM(CASE WHEN c.tipo = 'RECEITA' AND f.status = 'PENDENTE' THEN f.valor ELSE 0 END) AS receitasPendentes,
        SUM(CASE WHEN c.tipo = 'RECEITA' AND f.status = 'VENCIDA' THEN f.valor ELSE 0 END) AS receitasVencidas,
        SUM(CASE WHEN c.tipo = 'DESPESA' AND f.status IN ('PAGA', 'PAGAMENTO_PARCIAL') THEN COALESCE(f.valorPago, f.valor) ELSE 0 END) AS despesasPagas,
        SUM(CASE WHEN c.tipo = 'DESPESA' AND f.status = 'PENDENTE' THEN f.valor ELSE 0 END) AS despesasPendentes,
        SUM(CASE WHEN c.tipo = 'DESPESA' AND f.status = 'VENCIDA' THEN f.valor ELSE 0 END) AS despesasVencidas
    FROM Financeiro f
    JOIN f.conta c
    """)
    DashboardProjection obterResumoDashboardGeral();

    // Adicione junto aos outros métodos
    List<Financeiro> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT f FROM Financeiro f WHERE " +
            "(cast(:inicio as LocalDate) IS NULL OR f.dataVencimento >= :inicio) AND " +
            "(cast(:fim as LocalDate) IS NULL OR f.dataVencimento <= :fim) " +
            "ORDER BY f.dataVencimento")
    List<Financeiro> buscarParaRelatorio(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}