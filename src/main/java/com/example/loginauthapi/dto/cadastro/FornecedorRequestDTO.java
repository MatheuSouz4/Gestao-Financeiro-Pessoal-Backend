// Caminho: src/main/java/com/example/loginauthapi/dto/cadastro/FornecedorRequestDTO.java
package com.example.loginauthapi.dto.cadastro;

import jakarta.validation.constraints.NotBlank;

public record FornecedorRequestDTO(
        @NotBlank String razaoSocial,
        String nomeFantasia,
        @NotBlank String cpfCnpj, // RENOMEADO
        String email,
        String telefone,
        String endereco, // NOVO
        String descricao // NOVO
) {}