package com.example.loginauthapi.model;

import com.example.loginauthapi.dto.FornecedorRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FornecedorTest {

    @Test
    @DisplayName("Deve testar Getters, Setters e o Construtor vazio")
    void testGettersESetters() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(1L);

        assertEquals(1L, fornecedor.getId(), "O ID retornado deve ser igual ao configurado");
    }

    @Test
    @DisplayName("Deve instanciar um Fornecedor corretamente a partir do FornecedorRequestDTO")
    void testConstrutorComDTO() {
        // Criando o DTO com dados simulados
        FornecedorRequestDTO dto = new FornecedorRequestDTO(
                "Distribuidora de Alimentos SA",
                "Alimentos SA",
                TipoPessoa.JURIDICA,
                "12345678000199",
                "IS987654321",
                null,
                "contato@alimentos.com",
                "11988887777",
                "Avenida Principal, 1000",
                "Fornecedor de insumos básicos",
                Status.ATIVO
        );

        // Instanciando usando o construtor customizado
        Fornecedor fornecedor = new Fornecedor(dto);

        // Verificando se todos os campos herdados de Pessoa e específicos foram populados
        assertAll("Verificando dados populados pelo DTO",
                () -> assertEquals("Distribuidora de Alimentos SA", fornecedor.getNomeOuNomeFantasia()),
                () -> assertEquals("Alimentos SA", fornecedor.getRazaoSocial()),
                () -> assertEquals(TipoPessoa.JURIDICA, fornecedor.getTipoPessoa()),
                () -> assertEquals("12345678000199", fornecedor.getCpfCnpj()),
                () -> assertEquals("IS987654321", fornecedor.getInscricaoEstadual()),
                () -> assertNull(fornecedor.getRg()),
                () -> assertEquals("contato@alimentos.com", fornecedor.getEmail()),
                () -> assertEquals("11988887777", fornecedor.getTelefone()),
                () -> assertEquals("Avenida Principal, 1000", fornecedor.getEndereco()),
                () -> assertEquals("Fornecedor de insumos básicos", fornecedor.getDescricao()),
                () -> assertEquals(Status.ATIVO, fornecedor.getStatus())
        );
    }

    @Test
    @DisplayName("Deve testar equals, hashCode e canEqual gerados pelo Lombok (@EqualsAndHashCode de id)")
    void testMetodosLombok() {
        // Como o @EqualsAndHashCode(of = "id") foi usado, a igualdade depende apenas do ID
        Fornecedor f1 = new Fornecedor();
        f1.setId(1L);

        Fornecedor f2 = new Fornecedor();
        f2.setId(1L);

        Fornecedor f3 = new Fornecedor();
        f3.setId(2L);

        assertAll("Verificando métodos de comparação de objetos",
                // equals()
                () -> assertEquals(f1, f2, "Fornecedores com o mesmo ID devem ser iguais"),
                () -> assertNotEquals(f1, f3, "Fornecedores com IDs diferentes não devem ser iguais"),
                () -> assertNotEquals(f1, new Object(), "Não deve ser igual a um objeto de outra classe"),

                // hashCode()
                () -> assertEquals(f1.hashCode(), f2.hashCode(), "HashCodes devem ser iguais para IDs iguais"),
                () -> assertNotEquals(f1.hashCode(), f3.hashCode(), "HashCodes devem ser diferentes para IDs diferentes"),

                // canEqual()
                () -> assertTrue(f1.canEqual(f2), "f1 deve poder ser comparado com f2 (mesma classe)")
        );
    }
}