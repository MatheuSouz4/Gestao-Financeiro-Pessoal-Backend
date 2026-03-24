package com.example.loginauthapi.dto;

import java.math.BigDecimal;

public record DashboardResumoDTO(
        BigDecimal totalPago,
        BigDecimal totalPendente,
        Long qtdPendentes
) {}