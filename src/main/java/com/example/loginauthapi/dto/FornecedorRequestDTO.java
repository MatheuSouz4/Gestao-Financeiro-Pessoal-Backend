
package com.example.loginauthapi.dto;

import com.example.loginauthapi.model.Status;
import jakarta.validation.constraints.NotBlank;

public record FornecedorRequestDTO(
        @NotBlank String razaoSocial,
        @NotBlank String nomeFantasia,
        @NotBlank String cpfCnpj, // RENOMEADO
        String email,
        String telefone,
        String endereco, // NOVO
        String descricao, // NOVO
        Status status
) {}