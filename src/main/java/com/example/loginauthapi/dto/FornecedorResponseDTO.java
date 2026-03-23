
package com.example.loginauthapi.dto;

import com.example.loginauthapi.model.Fornecedor;
import com.example.loginauthapi.model.Status;
import java.time.LocalDateTime;

public record FornecedorResponseDTO(
        Long id,
        String razaoSocial,
        String nomeFantasia,
        String cpfCnpj,
        String email,
        String telefone,
        String endereco, 
        String descricao,
        Status status,
        LocalDateTime dataCadastro
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
                fornecedor.getDescricao(),
                fornecedor.getStatus(),
                fornecedor.getDataCadastro()
        );
    }
}