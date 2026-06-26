package com.example.loginauthapi.dto;

import java.math.BigDecimal;

public record ProjecaoMensalDTO(
        String mes,
        BigDecimal receitas,
        BigDecimal despesas
) {}