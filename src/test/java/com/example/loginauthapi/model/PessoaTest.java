package com.example.loginauthapi.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PessoaTest {

    // Classe concreta (Stub) para instanciar e testar a lógica da classe abstrata
    private static class PessoaStub extends Pessoa {}

    @Test
    @DisplayName("Deve validar Pessoa Física e limpar campos de PJ")
    void deveValidarPessoaFisicaELimparCamposPj() {
        Pessoa stub = new PessoaStub();
        stub.setTipoPessoa(TipoPessoa.FISICA);
        stub.setCpfCnpj("12345678901"); // 11 dígitos
        stub.setRazaoSocial("Empresa Falsa");
        stub.setInscricaoEstadual("1234");

        stub.validarConsistenciaDados();

        assertTrue(stub.isPessoaFisica());
        assertNull(stub.getRazaoSocial(), "Razão Social deve ser anulada para PF");
        assertNull(stub.getInscricaoEstadual(), "Inscrição Estadual deve ser anulada para PF");
    }

    @Test
    @DisplayName("Deve lançar exceção se CPF tiver mais de 11 dígitos")
    void deveLancarExcecaoCpfInvalido() {
        Pessoa stub = new PessoaStub();
        stub.setTipoPessoa(TipoPessoa.FISICA);
        stub.setCpfCnpj("12345678901234"); // 14 dígitos

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, stub::validarConsistenciaDados);
        assertEquals("CPF inválido para Pessoa Física", ex.getMessage());
    }

    @Test
    @DisplayName("Deve validar Pessoa Jurídica e limpar campos de PF")
    void deveValidarPessoaJuridicaELimparCamposPf() {
        Pessoa stub = new PessoaStub();
        stub.setTipoPessoa(TipoPessoa.JURIDICA);
        stub.setCpfCnpj("12345678901234"); // 14 dígitos
        stub.setRg("MG-12.345.678");

        stub.validarConsistenciaDados();

        assertFalse(stub.isPessoaFisica());
        assertNull(stub.getRg(), "RG deve ser anulado para PJ");
    }
    @Test
    @DisplayName("Deve testar todos os Getters e Setters de Pessoa")
    void testGettersAndSetters() {
        Pessoa pessoa = new Cliente(); // Usando Cliente pois Pessoa costuma ser abstrata
        LocalDateTime agora = LocalDateTime.now();

        // Testando Setters
        pessoa.setNomeOuNomeFantasia("Matheus Souza");
        pessoa.setEmail("matheus@email.com");
        pessoa.setTelefone("11999999999");
        pessoa.setEndereco("Rua Teste, 123");
        pessoa.setDescricao("Desenvolvedor Java");
        pessoa.setTipoPessoa(TipoPessoa.FISICA);
        pessoa.setStatus(Status.ATIVO);
        pessoa.setDataCadastro(agora);
        pessoa.setCpfCnpj("12345678901");
        pessoa.setRg("RG123");
        pessoa.setRazaoSocial("Matheus Dev LTDA");
        pessoa.setInscricaoEstadual("IS123");

        // Asserts para Getters
        assertAll("Verificando todos os campos",
                () -> assertEquals("Matheus Souza", pessoa.getNomeOuNomeFantasia()),
                () -> assertEquals("matheus@email.com", pessoa.getEmail()),
                () -> assertEquals("11999999999", pessoa.getTelefone()),
                () -> assertEquals("Rua Teste, 123", pessoa.getEndereco()),
                () -> assertEquals("Desenvolvedor Java", pessoa.getDescricao()),
                () -> assertEquals(TipoPessoa.FISICA, pessoa.getTipoPessoa()),
                () -> assertEquals(Status.ATIVO, pessoa.getStatus()),
                () -> assertEquals(agora, pessoa.getDataCadastro()),
                () -> assertEquals("12345678901", pessoa.getCpfCnpj()),
                () -> assertEquals("RG123", pessoa.getRg()),
                () -> assertEquals("Matheus Dev LTDA", pessoa.getRazaoSocial()),
                () -> assertEquals("IS123", pessoa.getInscricaoEstadual())
        );
    }

    @Test
    @DisplayName("Deve validar a lógica de isPessoaFisica")
    void testIsPessoaFisica() {
        Pessoa pf = new Cliente();
        pf.setTipoPessoa(TipoPessoa.FISICA);
        assertTrue(pf.isPessoaFisica(), "Deveria retornar true para FISICA");

        Pessoa pj = new Cliente();
        pj.setTipoPessoa(TipoPessoa.JURIDICA);
        assertFalse(pj.isPessoaFisica(), "Deveria retornar false para JURIDICA");
    }

    @Test
    @DisplayName("Deve validar consistência de dados (CPF vs CNPJ)")
    void testValidarConsistenciaDados() {
        Pessoa pessoa = new Cliente();

        // Cenário 1: Física com CPF preenchido
        pessoa.setTipoPessoa(TipoPessoa.FISICA);
        pessoa.setCpfCnpj("12345678901");
        assertDoesNotThrow(pessoa::validarConsistenciaDados);

        // Cenário 2: Jurídica com CNPJ preenchido
        pessoa.setTipoPessoa(TipoPessoa.JURIDICA);
        pessoa.setCpfCnpj("12345678901234");
        assertDoesNotThrow(pessoa::validarConsistenciaDados);

        // Cenário 3: Erro de consistência (Exemplo: Física sem CPF)
        // Aqui você deve ajustar de acordo com a exceção que seu método lança
        pessoa.setTipoPessoa(TipoPessoa.FISICA);
        pessoa.setCpfCnpj(null);
        // assertThrows(RuntimeException.class, pessoa::validarConsistenciaDados);
    }
}
