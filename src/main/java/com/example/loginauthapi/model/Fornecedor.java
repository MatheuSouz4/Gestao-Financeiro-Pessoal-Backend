package com.example.loginauthapi.model;

import com.example.loginauthapi.dto.FornecedorRequestDTO;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "fornecedores")
@Entity(name = "fornecedores")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "id")
public class Fornecedor extends Pessoa { // Aplicando a Herança

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Fornecedor(FornecedorRequestDTO data){
        super();
        this.setNomeOuNomeFantasia(data.nomeOuNomeFantasia());
        this.setRazaoSocial(data.RazaoSocial());
        this.setCpfCnpj(data.cpfCnpj());
        this.setInscricaoEstadual(data.inscricaoEstadual());
        this.setTipoPessoa(data.tipoPessoa());
        this.setEmail(data.email());
        this.setTelefone(data.telefone());
        this.setEndereco(data.endereco());
        this.setDescricao(data.descricao());
        this.setStatus(data.status());
    }
}