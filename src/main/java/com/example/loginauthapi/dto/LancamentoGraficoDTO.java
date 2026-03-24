package com.example.loginauthapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoGraficoDTO(
        LocalDate data,
        BigDecimal total
) {}