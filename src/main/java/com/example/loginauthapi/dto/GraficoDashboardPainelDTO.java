package com.example.loginauthapi.dto;

import java.math.BigDecimal;


public record GraficoDashboardPainelDTO(
        String periodo,
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal saldoEvolutivo
) {}