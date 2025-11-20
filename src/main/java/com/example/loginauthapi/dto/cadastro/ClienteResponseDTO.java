// Caminho: src/main/java/com/example/loginauthapi/dto/cadastro/ClienteResponseDTO.java
package com.example.loginauthapi.dto.cadastro;

import com.example.loginauthapi.domain.cadastro.Cliente;

// DTO para enviar dados de resposta (consulta)
public record ClienteResponseDTO(
        String id,
        String nome,
        String cpf_Cnpj, // NOVO
        String email,
        String telefone,
        String endereco, // NOVO
        String descricao // NOVO
) {
    public ClienteResponseDTO(Cliente cliente){
        this(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf_Cnpj(), // NOVO
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getEndereco(), // NOVO
                cliente.getDescricao() // NOVO
        );
    }
}