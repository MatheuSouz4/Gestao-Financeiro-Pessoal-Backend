package com.example.loginauthapi.dto;
import java.math.BigDecimal;

public record TopCategoriaDTO(
        String nome,
        BigDecimal total
) {}
