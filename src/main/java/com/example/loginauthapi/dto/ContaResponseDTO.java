package com.example.loginauthapi.dto;

import com.example.loginauthapi.model.Conta;
import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoConta;


public record ContaResponseDTO(
        Long id,
        String nome,
        TipoConta tipo,
        String descricao,
        Long clienteId,
        Long fornecedorId,
        Status status
) {
    public ContaResponseDTO(Conta conta) {
        this(
                conta.getId(),
                conta.getNome(),
                conta.getTipo(),
                conta.getDescricao(),
                conta.getClienteId(),
                conta.getFornecedorId(),
                conta.getStatus()
        );
    }
}