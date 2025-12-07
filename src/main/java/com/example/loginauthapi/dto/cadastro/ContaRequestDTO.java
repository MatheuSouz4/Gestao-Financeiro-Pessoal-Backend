package com.example.loginauthapi.dto.cadastro;


import com.example.loginauthapi.domain.cadastro.Recorrencia;
import com.example.loginauthapi.domain.cadastro.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContaRequestDTO(
        String  id,
        @NotBlank String nome,
        @NotNull TipoConta tipo,
        @NotNull Recorrencia recorrencia,
        String descricao,

        // 🚨 IDs de relacionamento (um será preenchido, o outro será null)
        String clienteId,
        String fornecedorId,

        String status
) {}