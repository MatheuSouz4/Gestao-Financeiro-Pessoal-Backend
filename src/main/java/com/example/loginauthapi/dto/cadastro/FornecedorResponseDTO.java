// Caminho: src/main/java/com/example/loginauthapi/dto/cadastro/FornecedorResponseDTO.java
package com.example.loginauthapi.dto.cadastro;

import com.example.loginauthapi.domain.cadastro.Fornecedor;

public record FornecedorResponseDTO(
        String id,
        String razaoSocial,
        String nomeFantasia,
        String cpfCnpj, // RENOMEADO
        String email,
        String telefone,
        String endereco, // NOVO
        String descricao // NOVO
) {
    public FornecedorResponseDTO(Fornecedor fornecedor) {
        this(
                fornecedor.getId(),
                fornecedor.getRazaoSocial(),
                fornecedor.getNomeFantasia(),
                fornecedor.getCpfCnpj(), // RENOMEADO
                fornecedor.getEmail(),
                fornecedor.getTelefone(),
                fornecedor.getEndereco(), // NOVO
                fornecedor.getDescricao() // NOVO
        );
    }
}