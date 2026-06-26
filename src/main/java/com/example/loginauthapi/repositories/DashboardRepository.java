package com.example.loginauthapi.repositories;

import com.example.loginauthapi.dto.DashboardProjection;
import com.example.loginauthapi.dto.TopCategoriaDTO;
import com.example.loginauthapi.model.Financeiro;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Financeiro, Long> {

    @Query("""
    SELECT 
        COALESCE(SUM(CASE WHEN c.tipo = 'RECEITA' AND f.status IN ('PAGA', 'PAGAMENTO_PARCIAL') THEN COALESCE(f.valorPago, f.valor) ELSE 0 END), 0) AS receitasRecebidas,
        COALESCE(SUM(CASE WHEN c.tipo = 'RECEITA' AND f.status = 'PENDENTE' THEN f.valor ELSE 0 END), 0) AS receitasPendentes,
        COALESCE(SUM(CASE WHEN c.tipo = 'RECEITA' AND f.status = 'VENCIDA' THEN f.valor ELSE 0 END), 0) AS receitasVencidas,
        COALESCE(SUM(CASE WHEN c.tipo = 'DESPESA' AND f.status IN ('PAGA', 'PAGAMENTO_PARCIAL') THEN COALESCE(f.valorPago, f.valor) ELSE 0 END), 0) AS despesasPagas,
        COALESCE(SUM(CASE WHEN c.tipo = 'DESPESA' AND f.status = 'PENDENTE' THEN f.valor ELSE 0 END), 0) AS despesasPendentes,
        COALESCE(SUM(CASE WHEN c.tipo = 'DESPESA' AND f.status = 'VENCIDA' THEN f.valor ELSE 0 END), 0) AS despesasVencidas
    FROM Financeiro f
    JOIN f.conta c
    WHERE (CAST(:inicio AS date) IS NULL OR f.dataVencimento >= CAST(:inicio AS date))
      AND (CAST(:fim AS date) IS NULL OR f.dataVencimento <= CAST(:fim AS date))
    """)
    DashboardProjection obterResumoDashboardGeralComFiltro(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("""
    SELECT new com.example.loginauthapi.dto.TopCategoriaDTO(c.nome, SUM(COALESCE(f.valorPago, f.valor)))
    FROM Financeiro f
    JOIN f.conta c
    WHERE c.tipo = 'RECEITA'
      AND f.status IN ('PAGA', 'PAGAMENTO_PARCIAL')
      AND (CAST(:inicio AS date) IS NULL OR f.dataVencimento >= CAST(:inicio AS date))
      AND (CAST(:fim AS date) IS NULL OR f.dataVencimento <= CAST(:fim AS date))
    GROUP BY c.nome
    ORDER BY SUM(COALESCE(f.valorPago, f.valor)) DESC
    """)
    List<TopCategoriaDTO> obterTopReceitas(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim, Pageable pageable);

    @Query("""
    SELECT new com.example.loginauthapi.dto.TopCategoriaDTO(c.nome, SUM(COALESCE(f.valorPago, f.valor)))
    FROM Financeiro f
    JOIN f.conta c
    WHERE c.tipo = 'DESPESA'
      AND f.status IN ('PAGA', 'PAGAMENTO_PARCIAL')
      AND (CAST(:inicio AS date) IS NULL OR f.dataVencimento >= CAST(:inicio AS date))
      AND (CAST(:fim AS date) IS NULL OR f.dataVencimento <= CAST(:fim AS date))
    GROUP BY c.nome
    ORDER BY SUM(COALESCE(f.valorPago, f.valor)) DESC
    """)
    List<TopCategoriaDTO> obterTopDespesas(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim, Pageable pageable);

    @Query("""
    SELECT f FROM Financeiro f 
    WHERE (CAST(:inicio AS date) IS NULL OR f.dataVencimento >= CAST(:inicio AS date))
      AND (CAST(:fim AS date) IS NULL OR f.dataVencimento <= CAST(:fim AS date))
    ORDER BY f.dataVencimento ASC
    """)
    List<Financeiro> findByDataVencimentoBetweenFiltro(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}