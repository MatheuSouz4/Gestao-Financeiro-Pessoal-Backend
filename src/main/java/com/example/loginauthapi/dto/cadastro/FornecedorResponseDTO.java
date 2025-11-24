
package com.example.loginauthapi.dto.cadastro;

import com.example.loginauthapi.domain.cadastro.Fornecedor;

public record FornecedorResponseDTO(
        String id,
        String razaoSocial,
        String nomeFantasia,
        String cpfCnpj,
        String email,
        String telefone,
        String endereco, 
        String descricao 
) {
    public FornecedorResponseDTO(Fornecedor fornecedor) {
        this(
                fornecedor.getId(),
                fornecedor.getRazaoSocial(),
                fornecedor.getNomeFantasia(),
                fornecedor.getCpfCnpj(),
                fornecedor.getEmail(),
                fornecedor.getTelefone(),
                fornecedor.getEndereco(), 
                fornecedor.getDescricao() 
        );
    }
}