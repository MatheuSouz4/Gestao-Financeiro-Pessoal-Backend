package com.example.loginauthapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GraficoDashboardDTO(
        LocalDate data,
        BigDecimal total
) {}