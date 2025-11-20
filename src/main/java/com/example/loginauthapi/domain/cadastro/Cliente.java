
package com.example.loginauthapi.domain.cadastro;
import com.example.loginauthapi.dto.cadastro.ClienteRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "clientes")
@Entity(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nome;

    @Column(unique = true)
    private String cpf_Cnpj;

    private String email;
    private String telefone;
    private String endereco;
    private String descricao;

    public Cliente(ClienteRequestDTO data){
        this.nome = data.nome();
        this.cpf_Cnpj = data.cpf_Cnpj();
        this.email = data.email();
        this.telefone = data.telefone();
        this.endereco = data.endereco();
        this.descricao = data.descricao();
    }
}