package com.example.loginauthapi.repositories;

import com.example.loginauthapi.dto.LancamentoGraficoDTO;
import com.example.loginauthapi.model.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    @Query("SELECT new com.example.loginauthapi.dto.LancamentoGraficoDTO(l.dataPagamento, SUM(l.valorPago)) " +
            "FROM Lancamento l " +
            "WHERE l.status = 'PAGA' " +
            "AND l.dataPagamento >= :inicio " +
            "GROUP BY l.dataPagamento " +
            "ORDER BY l.dataPagamento ASC")
    List<LancamentoGraficoDTO> buscarDadosGrafico(@Param("inicio") LocalDate inicio);
}
