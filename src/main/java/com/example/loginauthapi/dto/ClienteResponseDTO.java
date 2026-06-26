package com.example.loginauthapi.dto;

import com.example.loginauthapi.model.Cliente;
import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoPessoa;
import java.time.LocalDateTime;

public record ClienteResponseDTO(
        Long id,
        String nomeOuNomeFantasia,
        TipoPessoa tipoPessoa,
        String razaoSocial,
        String cpfCnpj,
        String inscricaoEstadual,
        String rg,
        String email,
        String telefone,
        String endereco,
        String descricao,
        Status status,
        LocalDateTime dataCadastro
) {
    public ClienteResponseDTO(Cliente cliente){
        this(
                cliente.getId(),
                cliente.getNomeOuNomeFantasia(),
                cliente.getTipoPessoa(),
                cliente.getRazaoSocial(),
                cliente.getCpfCnpj(),
                cliente.getInscricaoEstadual(),
                cliente.getRg(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getEndereco(),
                cliente.getDescricao(),
                cliente.getStatus(),
                cliente.getDataCadastro()
        );
    }
}