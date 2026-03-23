package com.example.loginauthapi.dto;

import jakarta.validation.constraints.*;
import com.example.loginauthapi.model.Status;


// DTO para receber dados de cadastro e atualização
public record ClienteRequestDTO(
        @NotBlank String nome,
        @NotBlank String cpfCnpj, // NOVO
        @Email String email,
        String telefone,
        String endereco, // NOVO
        String descricao, // NOVO
        Status status
) {
}