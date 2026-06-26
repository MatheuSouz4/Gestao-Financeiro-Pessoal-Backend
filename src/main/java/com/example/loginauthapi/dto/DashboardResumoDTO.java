package com.example.loginauthapi.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardResumoDTO {
    private BigDecimal receitasRecebidas;
    private BigDecimal receitasPendentes;
    private BigDecimal receitasVencidas;
    private BigDecimal despesasPagas;
    private BigDecimal despesasPendentes;
    private BigDecimal despesasVencidas;
}