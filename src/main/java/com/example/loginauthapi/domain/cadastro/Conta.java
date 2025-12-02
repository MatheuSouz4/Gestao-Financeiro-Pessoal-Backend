package com.example.loginauthapi.domain.cadastro;


import jakarta.persistence.*;
import lombok.*;

// 🚨 Tipos de Enums para Recorrência e Tipo
public enum TipoConta {
    RECEITA, DESPESA
}

public enum Recorrencia {
    UNICA, SEMANAL, MENSAL, TRIMESTRAL, SEMESTRAL, ANUAL
}


@Table(name = "contas")
@Entity(name = "Conta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConta tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Recorrencia recorrencia;

    private String descricao;

    // 🚨 Relacionamento (Chaves Estrangeiras - Pode ser null)
    // Armazena o ID do Cliente se for RECEITA
    @Column(name = "cliente_id", nullable = true)
    private String clienteId;

    // Armazena o ID do Fornecedor se for DESPESA
    @Column(name = "fornecedor_id", nullable = true)
    private String fornecedorId;

    // Status (Opcional, se o ciclo de vida da conta base for controlado)
    private String status = "ATIVO";

    public Conta(ContaRequestDTO data){
        this.nome = data.nome();
        this.tipo = data.tipo();
        this.recorrencia = data.recorrencia();
        this.descricao = data.descricao();

        // Trata a atribuição condicional dos IDs
        if (this.tipo == TipoConta.RECEITA) {
            this.clienteId = data.clienteId();
            this.fornecedorId = null;
        } else if (this.tipo == TipoConta.DESPESA) {
            this.fornecedorId = data.fornecedorId();
            this.clienteId = null;
        }

        // Status pode vir do DTO ou ser padrão
        this.status = data.status() != null ? data.status() : "ATIVO";
    }
}