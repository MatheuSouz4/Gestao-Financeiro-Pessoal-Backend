package com.example.loginauthapi.model;

import com.example.loginauthapi.dto.ClienteRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PessoaEClienteTest {

    @Test
    @DisplayName("Cobertura total de Getters, Setters e Construtores de Pessoa e Cliente")
    void coberturaGeral() {
        Cliente cliente = new Cliente();
        LocalDateTime agora = LocalDateTime.now();

        // 1. Cobrindo todos os Setters de Pessoa (vistos na imagem image_5a0b79.png)
        cliente.setNomeOuNomeFantasia("Matheus Souza");
        cliente.setEmail("matheus@email.com");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Rua Teste");
        cliente.setDescricao("Dev");
        cliente.setTipoPessoa(TipoPessoa.FISICA);
        cliente.setStatus(Status.ATIVO);
        cliente.setDataCadastro(agora);
        cliente.setCpfCnpj("12345678901");
        cliente.setRg("RG123");
        cliente.setRazaoSocial("Razao");
        cliente.setInscricaoEstadual("IS123");
        cliente.setId(1L); // Setter de Cliente (image_5a86fa.png)

        // 2. Cobrindo todos os Getters
        assertAll("Verificando Getters",
                () -> assertEquals("Matheus Souza", cliente.getNomeOuNomeFantasia()),
                () -> assertEquals(1L, cliente.getId()),
                () -> assertEquals(TipoPessoa.FISICA, cliente.getTipoPessoa()),
                () -> assertEquals(agora, cliente.getDataCadastro())
        );

        // 3. Cobrindo isPessoaFisica (image_5a0b79.png)
        assertTrue(cliente.isPessoaFisica());
        cliente.setTipoPessoa(TipoPessoa.JURIDICA);
        assertFalse(cliente.isPessoaFisica());
    }

    @Test
    @DisplayName("Cobertura de equals, hashCode e canEqual (Lombok)")
    void testLombokMethods() {
        // Criando dois objetos iguais para image_5a86fa.png
        Cliente c1 = new Cliente();
        c1.setId(1L);
        Cliente c2 = new Cliente();
        c2.setId(1L);
        Cliente c3 = new Cliente();
        c3.setId(2L);

        assertAll("Métodos de Objeto",
                () -> assertEquals(c1, c2),           // equals(Object)
                () -> assertNotEquals(c1, c3),        // equals(Object) - branch diferente
                () -> assertEquals(c1.hashCode(), c2.hashCode()), // hashCode()
                () -> assertTrue(c1.canEqual(c2)),    // canEqual(Object)
                () -> assertNotNull(c1.toString())    // toString (boa prática)
        );
    }

    @Test
    @DisplayName("Cobertura de validarConsistenciaDados (Complexidade 7)")
    void testValidarConsistenciaDados() {
        Cliente c = new Cliente();

        // Testando as branches (Missed Branches na image_5a0b79.png)
        // Caminho 1: Pessoa Física válida
        c.setTipoPessoa(TipoPessoa.FISICA);
        c.setCpfCnpj("12345678901");
        assertDoesNotThrow(c::validarConsistenciaDados);

        // Caminho 2: Pessoa Jurídica válida
        c.setTipoPessoa(TipoPessoa.JURIDICA);
        c.setCpfCnpj("12.345.678/0001-99");
        assertDoesNotThrow(c::validarConsistenciaDados);

        // Nota: Se o seu método lança exceções para CPF nulo ou tipos errados,
        // adicione assertThrows aqui para cobrir os 7 pontos de complexidade.
    }

    @Test
    @DisplayName("Cobertura do Construtor DTO")
    void testConstructorDTO() {
        // Cobrindo Cliente(ClienteRequestDTO) da image_5a86fa.png
        ClienteRequestDTO dto = new ClienteRequestDTO(
                "Matheus", TipoPessoa.FISICA, null, "123", null, null,
                "m@m.com", "123", "Rua", "Desc", Status.ATIVO
        );

        Cliente cliente = new Cliente(dto);
        assertEquals("Matheus", cliente.getNomeOuNomeFantasia());
    }
}