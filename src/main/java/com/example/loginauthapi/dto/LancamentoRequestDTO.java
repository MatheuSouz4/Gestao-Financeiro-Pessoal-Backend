package com.example.loginauthapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoRequestDTO(
        Long id,
        Long contaId,
        LocalDate vencimento, // Nomeado como no seu lancamentos-form.component.ts
        BigDecimal valor,
        String descricao
) {}