
package com.example.loginauthapi.dto.cadastro;

import jakarta.validation.constraints.NotBlank;

// DTO para receber dados de cadastro e atualização
public record ClienteRequestDTO(
        @NotBlank String nome,
        @NotBlank String cpf_Cnpj, // NOVO
        String email,
        String telefone,
        String endereco, // NOVO
        String descricao, // NOVO
        String status
) {
}