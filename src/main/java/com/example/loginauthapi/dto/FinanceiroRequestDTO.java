package com.example.loginauthapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinanceiroRequestDTO(
        Long id,
        Long contaId,
        LocalDate vencimento,
        BigDecimal valor,
        String descricao
) {}