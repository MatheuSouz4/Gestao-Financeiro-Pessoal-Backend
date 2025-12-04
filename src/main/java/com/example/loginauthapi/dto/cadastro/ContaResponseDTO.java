package com.example.loginauthapi.dto.cadastro;

import com.example.loginauthapi.domain.cadastro.Conta;
import com.example.loginauthapi.domain.cadastro.Recorrencia;
import com.example.loginauthapi.domain.cadastro.TipoConta;


public record ContaResponseDTO(
        String id,
        String nome,
        TipoConta tipo,
        Recorrencia recorrencia,
        String descricao,
        String clienteId,
        String fornecedorId,
        String status
) {
    public ContaResponseDTO(Conta conta) {
        this(
                conta.getId(),
                conta.getNome(),
                conta.getTipo(),
                conta.getRecorrencia(),
                conta.getDescricao(),
                conta.getClienteId(),
                conta.getFornecedorId(),
                conta.getStatus()
        );
    }
}