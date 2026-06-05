package com.example.loginauthapi.dto;

import java.math.BigDecimal;

public record ResumoDashboardDTO(
        BigDecimal ReceitasRecebidas,
        BigDecimal ReceitasPendentes,
        BigDecimal ReceitasVencidas,
        BigDecimal DespesasVencidas,
        BigDecimal DespesasPagas,
        BigDecimal DespesasPendentes,
        BigDecimal SaldoPago,
        BigDecimal SaldoPendente,
        Long QtdPendentes
) {}