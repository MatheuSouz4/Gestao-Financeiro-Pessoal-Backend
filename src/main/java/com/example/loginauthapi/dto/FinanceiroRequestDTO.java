package com.example.loginauthapi.dto;

import com.example.loginauthapi.model.TipoRecorrencia;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FinanceiroRequestDTO(
        Long id,
        Long contaId,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate vencimento,

        BigDecimal valor,
        String descricao,
        TipoRecorrencia tipoRecorrencia,
        Integer quantidadeParcelas,
        String motivoAlteracao,
        String justificativaEstorno
) {}