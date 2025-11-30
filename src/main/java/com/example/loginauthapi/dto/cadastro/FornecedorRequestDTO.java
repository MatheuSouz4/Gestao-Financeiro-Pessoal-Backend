
package com.example.loginauthapi.dto.cadastro;

import jakarta.validation.constraints.NotBlank;

public record FornecedorRequestDTO(
        @NotBlank String razaoSocial,
        @NotBlank String nomeFantasia,
        @NotBlank String cpf_Cnpj, // RENOMEADO
        String email,
        String telefone,
        String endereco, // NOVO
        String descricao, // NOVO
        String status
) {}