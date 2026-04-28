package com.example.loginauthapi.dto;

import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContaRequestDTO(
        Long id,
        @NotBlank(message = "O nome da conta é obrigatório")
        String nome,
        @NotNull(message = "O tipo de conta (RECEITA/DESPESA) deve ser informado")
        TipoConta tipo,
        String descricao,
        Long clienteId,
        Long fornecedorId,
        Status status
) {}