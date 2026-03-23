package com.example.loginauthapi.model;

import com.example.loginauthapi.dto.ClienteRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;


@Table(name = "clientes")
@Entity(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Column(unique = true)
    private String cpfCnpj;
    private String email;
    private String telefone;
    private String endereco;
    private String descricao;

    @Enumerated(EnumType.STRING) // Salva o nome do Enum no banco (ex: "ATIVO")
    private Status status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCadastro; // Importante para relatórios financeiros

    public Cliente(ClienteRequestDTO data){
        this.nome = data.nome();
        this.cpfCnpj = data.cpfCnpj();
        this.email = data.email();
        this.telefone = data.telefone();
        this.endereco = data.endereco();
        this.descricao = data.descricao();
        this.status = data.status();
    }
}