
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
    private String cpfCnpj;

    private String email;
    private String telefone;
    private String endereco;
    private String descricao;

    public Cliente(ClienteRequestDTO data){
        this.nome = data.nome();
        this.cpfCnpj = data.cpfCnpj();
        this.email = data.email();
        this.telefone = data.telefone();
        this.endereco = data.endereco();
        this.descricao = data.descricao();
    }
}