package com.example.loginauthapi.dto;

import com.example.loginauthapi.model.TipoRecorrencia;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FinanceiroRequestDTO(
        Long id,
        Long contaId,
        LocalDate vencimento,
        BigDecimal valor,
        String descricao,
        TipoRecorrencia tipoRecorrencia,
        Integer quantidadeParcelas, // Obrigatório se a recorrência não for NENHUMA
        String motivoAlteracao
) {
    public static FinanceiroRequestDTO simples(Long contaId, LocalDate venc, BigDecimal valor, String desc) {
        return new FinanceiroRequestDTO(null, contaId, venc, valor, desc, null, 1, null);
    }
}