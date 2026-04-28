package com.example.loginauthapi.model;

import com.example.loginauthapi.dto.ContaRequestDTO;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "contas")
@Entity(name = "conta")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder // Facilita a criação em testes
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

    private String descricao;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "fornecedor_id")
    private Long fornecedorId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVO;

    public Conta(ContaRequestDTO data) {
        this.atualizarDados(data);
    }

    public void atualizarDados(ContaRequestDTO data) {
        this.nome = data.nome();
        this.tipo = data.tipo();
        this.descricao = data.descricao();
        this.status = data.status() != null ? data.status() : Status.ATIVO;
        this.ajustarRelacionamentos(data.clienteId(), data.fornecedorId());
    }

    private void ajustarRelacionamentos(Long clienteId, Long fornecedorId) {
        if (this.tipo == TipoConta.RECEITA) {
            this.clienteId = clienteId;
            this.fornecedorId = null;
        } else {
            this.fornecedorId = fornecedorId;
            this.clienteId = null;
        }
    }
}