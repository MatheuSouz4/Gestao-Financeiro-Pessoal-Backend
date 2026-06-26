package com.example.loginauthapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@MappedSuperclass // Fundamental: Diz ao JPA para herdar essas colunas nas tabelas filhas
@Getter
@Setter
public abstract class Pessoa { // 'abstract' impede que 'Pessoa' seja instanciada diretamente

    // Dados Comuns
    private String nomeOuNomeFantasia;
    private String email;
    private String telefone;
    private String endereco;
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPessoa tipoPessoa; // Define se é PF ou PJ

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCadastro;

    // Dados Específicos (Ficam nulos dependendo do TipoPessoa)
    @Column(unique = true, nullable = false)
    private String cpfCnpj;

    private String rg; // Apenas para PF
    private String razaoSocial; // Apenas para PJ
    private String inscricaoEstadual; // Apenas para PJ

    // Construtor vazio para o JPA
    public Pessoa() {}

    // Exemplo de Polimorfismo: um método que pode ser sobrescrito
    public boolean isPessoaFisica() {
        return this.tipoPessoa == TipoPessoa.FISICA;
    }

    // Dentro da classe Pessoa.java

    public void validarConsistenciaDados() {
        if (this.tipoPessoa == TipoPessoa.FISICA) {
            // Se for Física, não pode ter dados de empresa
            this.razaoSocial = null;
            this.inscricaoEstadual = null;

            if (this.cpfCnpj != null && this.cpfCnpj.length() > 11) {
                throw new IllegalArgumentException("CPF inválido para Pessoa Física");
            }
        } else if (this.tipoPessoa == TipoPessoa.JURIDICA) {
            // Se for Jurídica, o RG não faz sentido
            this.rg = null;

            if (this.cpfCnpj != null && this.cpfCnpj.length() <= 11) {
                throw new IllegalArgumentException("CNPJ inválido para Pessoa Jurídica");
            }
        }
    }

}