package com.example.loginauthapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record QuitacaoRequestDTO(
        LocalDate dataPagamento,
        BigDecimal valorPago,
        String comprovanteUrl
) {}