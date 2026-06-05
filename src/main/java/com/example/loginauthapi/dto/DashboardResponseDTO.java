package com.example.loginauthapi.dto;

import java.math.BigDecimal;

public record DashboardResponseDTO(
        BigDecimal receitasRecebidas,
        BigDecimal receitasPendentes,
        BigDecimal receitasVencidas,
        BigDecimal saldoReceitas,
        BigDecimal despesasPagas,
        BigDecimal despesasPendentes,
        BigDecimal despesasVencidas,
        BigDecimal saldoDespesas,
        BigDecimal saldoGeral
) {
    // Construtor customizado para garantir que nulls virem ZERO e para calcular os saldos totais no backend
    public DashboardResponseDTO(
            BigDecimal receitasRecebidas, BigDecimal receitasPendentes, BigDecimal receitasVencidas,
            BigDecimal despesasPagas, BigDecimal despesasPendentes, BigDecimal despesasVencidas
    ) {
        this(
                receitasRecebidas != null ? receitasRecebidas : BigDecimal.ZERO,
                receitasPendentes != null ? receitasPendentes : BigDecimal.ZERO,
                receitasVencidas != null ? receitasVencidas : BigDecimal.ZERO,

                // Saldo Receitas = Recebidas + Pendentes
                (receitasRecebidas != null ? receitasRecebidas : BigDecimal.ZERO)
                        .add(receitasPendentes != null ? receitasPendentes : BigDecimal.ZERO),

                despesasPagas != null ? despesasPagas : BigDecimal.ZERO,
                despesasPendentes != null ? despesasPendentes : BigDecimal.ZERO,
                despesasVencidas != null ? despesasVencidas : BigDecimal.ZERO,

                // Saldo Despesas = Pagas + Pendentes
                (despesasPagas != null ? despesasPagas : BigDecimal.ZERO)
                        .add(despesasPendentes != null ? despesasPendentes : BigDecimal.ZERO),

                // Saldo Geral = Receitas Recebidas - Despesas Pagas
                (receitasRecebidas != null ? receitasRecebidas : BigDecimal.ZERO)
                        .subtract(despesasPagas != null ? despesasPagas : BigDecimal.ZERO)
        );
    }
}