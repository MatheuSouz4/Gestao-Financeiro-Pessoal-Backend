package com.example.loginauthapi.dto;

import com.example.loginauthapi.model.Cliente;
import com.example.loginauthapi.model.Status;
import java.time.LocalDateTime;

// DTO para enviar dados de resposta (consulta)
public record ClienteResponseDTO(
        Long id,
        String nome,
        String cpfCnpj, // NOVO
        String email,
        String telefone,
        String endereco, // NOVO
        String descricao, // NOVO
        Status status,
        LocalDateTime dataCadastro
) {
    public ClienteResponseDTO(Cliente cliente){
        this(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpfCnpj(), // NOVO
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getEndereco(), // NOVO
                cliente.getDescricao(), // NOVO
                cliente.getStatus(),
                cliente.getDataCadastro()
        );
    }
}