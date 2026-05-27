package com.example.loginauthapi.dto;

import java.math.BigDecimal;

public record ResumoDashboardDTO(
        BigDecimal ReceitasRecebidas,
        BigDecimal ReceitasPendentes,
        BigDecimal DespesasPagas,
        BigDecimal DespesasPendentes,
        BigDecimal SaldoPago,
        BigDecimal SaldoPendente,
        Long QtdPendentes
) {}