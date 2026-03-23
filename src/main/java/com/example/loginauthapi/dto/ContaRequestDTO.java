package com.example.loginauthapi.dto;


import com.example.loginauthapi.model.Recorrencia;
import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContaRequestDTO(
        Long id,
        @NotBlank String nome,
        @NotNull TipoConta tipo,
        @NotNull Recorrencia recorrencia,
        String descricao,
        Long clienteId,
        Long fornecedorId,
        Status status
) {}