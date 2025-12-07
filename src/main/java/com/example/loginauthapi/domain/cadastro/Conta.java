package com.example.loginauthapi.domain.cadastro;


import com.example.loginauthapi.dto.cadastro.ContaRequestDTO;
import jakarta.persistence.*;
import lombok.*;


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
    private String cliente;

    // Armazena o ID do Fornecedor se for DESPESA
    @Column(name = "fornecedor_id", nullable = true)
    private String fornecedor;

    // Status (Opcional, se o ciclo de vida da conta base for controlado)
    private String status = "ATIVO";

    public Conta(ContaRequestDTO data){
        this.nome = data.nome();
        this.tipo = data.tipo();
        this.recorrencia = data.recorrencia();
        this.descricao = data.descricao();

        // Trata a atribuição condicional dos IDs
        if (this.tipo == TipoConta.RECEITA) {
            this.cliente = data.clienteId();
            this.fornecedor = null;
        } else if (this.tipo == TipoConta.DESPESA) {
            this.fornecedor = data.fornecedorId();
            this.cliente = null;
        }

        // Status pode vir do DTO ou ser padrão
        this.status = data.status() != null ? data.status() : "ATIVO";
    }
}