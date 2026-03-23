package com.example.loginauthapi.model;

import com.example.loginauthapi.dto.ContaRequestDTO;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "contas")
@Entity(name = "Conta")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConta tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Recorrencia recorrencia;

    private String descricao;

    // Correção Crítica: Usar Long (maiúsculo) para aceitar null
    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "fornecedor_id")
    private Long fornecedorId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVO;

    public Conta(ContaRequestDTO data){
        this.nome = data.nome();
        this.tipo = data.tipo();
        this.recorrencia = data.recorrencia();
        this.descricao = data.descricao();
        this.status = data.status() != null ? data.status() : Status.ATIVO;

        atribuirRelacionamentos(data);
    }

    // Método encapsulado para não poluir o construtor
    public void atribuirRelacionamentos(ContaRequestDTO data) {
        if (this.tipo == TipoConta.RECEITA) {
            this.clienteId = data.clienteId();
            this.fornecedorId = null;
        } else if (this.tipo == TipoConta.DESPESA) {
            this.fornecedorId = data.fornecedorId();
            this.clienteId = null;
        }
    }
}