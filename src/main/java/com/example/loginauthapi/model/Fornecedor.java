
package com.example.loginauthapi.model;

import com.example.loginauthapi.dto.FornecedorRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "fornecedores")
@Entity(name = "fornecedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Fornecedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(nullable = false, unique = true)
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

    public Fornecedor(FornecedorRequestDTO data){
        this.razaoSocial = data.razaoSocial();
        this.nomeFantasia = data.nomeFantasia();
        this.cpfCnpj = data.cpfCnpj();
        this.email = data.email();
        this.telefone = data.telefone();
        this.endereco = data.endereco(); 
        this.descricao = data.descricao();
        this.status = data.status();
    }
}