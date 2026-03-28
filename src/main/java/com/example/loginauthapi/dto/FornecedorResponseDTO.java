package com.example.loginauthapi.dto;

import com.example.loginauthapi.model.Fornecedor;
import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoPessoa;
import java.time.LocalDateTime;

public record FornecedorResponseDTO(
        Long id,
        String nomeOuRazaoSocial,
        String nomeFantasia,
        TipoPessoa tipoPessoa,
        String cpfCnpj,
        String inscricaoEstadual,
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
                fornecedor.getNomeOuNomeFantasia(),
                fornecedor.getRazaoSocial(),
                fornecedor.getTipoPessoa(),
                fornecedor.getCpfCnpj(),
                fornecedor.getInscricaoEstadual(),
                fornecedor.getEmail(),
                fornecedor.getTelefone(),
                fornecedor.getEndereco(),
                fornecedor.getDescricao(),
                fornecedor.getStatus(),
                fornecedor.getDataCadastro()
        );
    }
}