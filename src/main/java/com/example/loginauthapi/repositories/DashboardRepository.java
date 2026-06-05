package com.example.loginauthapi.repositories;

import com.example.loginauthapi.model.Financeiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface DashboardRepository extends JpaRepository<Financeiro, Long> {

    @Query(value = """
        SELECT 
            COALESCE(SUM(CASE WHEN c.tipo = 'RECEITA' AND f.status = 'PAGA' THEN f.valor ELSE 0 END), 0) as receitasRecebidas,
            COALESCE(SUM(CASE WHEN c.tipo = 'RECEITA' AND f.status = 'PENDENTE' THEN f.valor ELSE 0 END), 0) as receitasPendentes,
            COALESCE(SUM(CASE WHEN c.tipo = 'DESPESA' AND f.status = 'PAGA' THEN f.valor ELSE 0 END), 0) as despesasPagas,
            COALESCE(SUM(CASE WHEN c.tipo = 'DESPESA' AND f.status = 'PENDENTE' THEN f.valor ELSE 0 END), 0) as despesasPendentes,
            COALESCE(SUM(CASE WHEN c.tipo = 'RECEITA' AND f.status = 'PENDENTE' AND f.data_vencimento < :hoje THEN f.valor ELSE 0 END), 0) as receitasVencidas,
            COALESCE(SUM(CASE WHEN c.tipo = 'DESPESA' AND f.status = 'PENDENTE' AND f.data_vencimento < :hoje THEN f.valor ELSE 0 END), 0) as despesasVencidas,
            COUNT(CASE WHEN f.status = 'PENDENTE' THEN 1 END) as qtdPendentes
        FROM financeiro f
        INNER JOIN conta c ON f.conta_id = c.id
        WHERE (CAST(:contaId AS TEXT) IS NULL OR f.conta_id = CAST(CAST(:contaId AS TEXT) AS BIGINT))
          AND (f.data_vencimento BETWEEN :inicio AND :fim)
        """, nativeQuery = true)
    Map<String, Object> obterResumoDashboardRaw(
            @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim,
            @Param("contaId") Long contaId, @Param("hoje") LocalDate hoje);

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN f.conta.tipo = 'RECEITA' THEN f.valor ELSE -f.valor END), 0)
        FROM Financeiro f
        WHERE f.status = 'PAGA' AND f.dataPagamento <= :hoje
          AND (:contaId IS NULL OR f.conta.id = :contaId)
        """)
    BigDecimal obterSaldoConsolidadoAteHoje(@Param("hoje") LocalDate hoje, @Param("contaId") Long contaId);

    @Query("""
    SELECT f FROM Financeiro f 
    WHERE f.status = 'PENDENTE' 
      AND f.dataVencimento BETWEEN :hoje AND :dataLimite
      AND (:contaId IS NULL OR f.conta.id = :contaId)
    """)
    List<Financeiro> findFuturosPendentes(@Param("hoje") LocalDate hoje,
                                          @Param("dataLimite") LocalDate dataLimite,
                                          @Param("contaId") Long contaId);

    @Query(value = """
        SELECT TO_CHAR(f.data_vencimento, 'DD/MM') as periodo,
            COALESCE(SUM(CASE WHEN c.tipo = 'RECEITA' THEN f.valor ELSE 0 END), 0) as totalReceitas,
            COALESCE(SUM(CASE WHEN c.tipo = 'DESPESA' THEN f.valor ELSE 0 END), 0) as totalDespesas
        FROM financeiro f
        INNER JOIN conta c ON f.conta_id = c.id
        WHERE f.data_vencimento BETWEEN :inicio AND :fim
          AND (CAST(:contaId AS TEXT) IS NULL OR f.conta_id = CAST(CAST(:contaId AS TEXT) AS BIGINT))
        GROUP BY TO_CHAR(f.data_vencimento, 'DD/MM'), f.data_vencimento
        ORDER BY f.data_vencimento ASC
        """, nativeQuery = true)
    List<Map<String, Object>> obterDadosGraficoAgrupado(@Param("inicio") LocalDate inicio,
                                                        @Param("fim") LocalDate fim,
                                                        @Param("contaId") Long contaId);
}