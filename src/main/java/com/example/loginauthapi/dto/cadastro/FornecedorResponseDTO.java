
package com.example.loginauthapi.dto.cadastro;

import com.example.loginauthapi.domain.cadastro.Fornecedor;

public record FornecedorResponseDTO(
        String id,
        String razaoSocial,
        String nomeFantasia,
        String cpf_Cnpj,
        String email,
        String telefone,
        String endereco, 
        String descricao,
        String status
) {
    public FornecedorResponseDTO(Fornecedor fornecedor) {
        this(
                fornecedor.getId(),
                fornecedor.getRazaoSocial(),
                fornecedor.getNomeFantasia(),
                fornecedor.getCpf_Cnpj(),
                fornecedor.getEmail(),
                fornecedor.getTelefone(),
                fornecedor.getEndereco(), 
                fornecedor.getDescricao(),
                fornecedor.getStatus()
        );
    }
}