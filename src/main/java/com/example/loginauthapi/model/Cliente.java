package com.example.loginauthapi.model;

import com.example.loginauthapi.dto.ClienteRequestDTO;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "clientes")
@Entity(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "id")
public class Cliente extends Pessoa { // Aplicando a Herança

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Construtor usando o DTO
    public Cliente(ClienteRequestDTO data){
        super();
        this.setNomeOuNomeFantasia(data.nomeOuNomeFantasia());
        this.setRazaoSocial(data.razaoSocial());
        this.setCpfCnpj(data.cpfCnpj());
        this.setInscricaoEstadual(data.inscricaoEstadual());
        this.setRg(data.rg());
        this.setTipoPessoa(data.tipoPessoa());
        this.setEmail(data.email());
        this.setTelefone(data.telefone());
        this.setEndereco(data.endereco());
        this.setDescricao(data.descricao());
        this.setStatus(data.status());
    }
}