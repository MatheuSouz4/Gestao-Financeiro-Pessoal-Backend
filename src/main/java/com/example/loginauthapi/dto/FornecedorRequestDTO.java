package com.example.loginauthapi.dto;

import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoPessoa;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record FornecedorRequestDTO(
        @NotBlank(message = "O nome ou razão social é obrigatório") String nomeOuNomeFantasia,
        String razaoSocial, // Geralmente aplicável a Pessoa Jurídica
        @NotNull(message = "O tipo de pessoa (FISICA ou JURIDICA) é obrigatório") TipoPessoa tipoPessoa,
        @NotBlank(message = "O CPF/CNPJ é obrigatório") String cpfCnpj,
        String inscricaoEstadual, // Exclusivo para Pessoa Jurídica
        String rg,
        @Email(message = "Formato de e-mail inválido") String email,
        String telefone,
        String endereco,
        String descricao,
        Status status
) {}