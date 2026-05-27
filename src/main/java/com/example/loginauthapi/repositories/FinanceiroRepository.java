package com.example.loginauthapi.repositories;

import com.example.loginauthapi.dto.GraficoDashboardDTO;
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

    List<Financeiro> findByStatusInAndDataVencimentoBefore(List<StatusLancamento> statuses, LocalDate data);

    List<Financeiro> findByIdReferencia(Long idReferencia);

    // Soma todas as RECEITAS pagas/parciais de uma conta específica
    @Query("SELECT COALESCE(SUM(f.valorPago), 0) FROM Financeiro f " +
            "WHERE f.conta.id = :contaId " +
            "AND f.conta.tipo = 'RECEITA' " + // <-- Corrigido para f.conta.tipo
            "AND f.status IN ('PAGA', 'PAGAMENTO_PARCIAL')")
    BigDecimal sumReceitasRecebidasByConta(@Param("contaId") Long contaId);

    // Soma todas as DESPESAS pagas/parciais de uma conta específica
    @Query("SELECT COALESCE(SUM(f.valorPago), 0) FROM Financeiro f " +
            "WHERE f.conta.id = :contaId " +
            "AND f.conta.tipo = 'DESPESA' " + // <-- Corrigido para f.conta.tipo
            "AND f.status IN ('PAGA', 'PAGAMENTO_PARCIAL')")
    BigDecimal sumDespesasPagasByConta(@Param("contaId") Long contaId);

    // Contagem de pendentes (inclui vencidas)
    @Query("SELECT COUNT(f) FROM Financeiro f" + " WHERE f.conta.tipo = 'DESPESA' " + "AND f.status IN ('PENDENTE', 'VENCIDA')")
    Long countPendentes();

    // Receitas Pagas (Soma o que de fato foi pago)
    @Query("SELECT COALESCE(SUM(f.valorPago), 0) FROM Financeiro f WHERE f.conta.tipo = 'RECEITA' AND f.status IN ('PAGA', 'PAGAMENTO_PARCIAL')") // <-- Corrigido
    BigDecimal sumReceitasRecebidas();

    // Receitas Pendentes (Soma o valor original do título)
    @Query("SELECT COALESCE(SUM(f.valor), 0) FROM Financeiro f WHERE f.conta.tipo = 'RECEITA' AND f.status IN ('PENDENTE', 'VENCIDA')") // <-- Corrigido
    BigDecimal sumReceitasPendentes();

    // Despesas Pagas (Soma o que de fato foi pago)
    @Query("SELECT COALESCE(SUM(f.valorPago), 0) FROM Financeiro f WHERE f.conta.tipo = 'DESPESA' AND f.status IN ('PAGA', 'PAGAMENTO_PARCIAL')") // <-- Corrigido
    BigDecimal sumDespesasPagas();

    // Despesas Pendentes (Soma o valor original do título)
    @Query("SELECT COALESCE(SUM(f.valor), 0) FROM Financeiro f WHERE f.conta.tipo = 'DESPESA' AND f.status IN ('PENDENTE', 'VENCIDA')") // <-- Corrigido
    BigDecimal sumDespesasPendentes();

    // Dados para o Gráfico (Agrupados por data de pagamento)
    @Query("SELECT new com.example.loginauthapi.dto.GraficoDashboardDTO(f.dataPagamento, SUM(f.valorPago)) " +
            "FROM Financeiro f WHERE f.status IN ('PAGA', 'PAGAMENTO_PARCIAL') AND f.dataPagamento >= :desde " +
            "GROUP BY f.dataPagamento ORDER BY f.dataPagamento ASC")
    List<GraficoDashboardDTO> buscarDadosGrafico(@Param("desde") LocalDate desde);
}