package com.example.loginauthapi.dto;

import java.math.BigDecimal;

public interface DashboardProjection {
    BigDecimal getReceitasRecebidas();
    BigDecimal getReceitasPendentes();
    BigDecimal getReceitasVencidas();
    BigDecimal getDespesasPagas();
    BigDecimal getDespesasPendentes();
    BigDecimal getDespesasVencidas();
}