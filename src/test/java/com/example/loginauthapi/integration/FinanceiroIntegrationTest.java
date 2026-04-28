package com.example.loginauthapi.integration;

import com.example.loginauthapi.dto.FinanceiroRequestDTO;
import com.example.loginauthapi.model.Conta;
import com.example.loginauthapi.model.Financeiro;
import com.example.loginauthapi.model.StatusLancamento;
import com.example.loginauthapi.model.TipoConta;
import com.example.loginauthapi.repositories.ContaRepository;
import com.example.loginauthapi.services.FinanceiroService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Muito importante: limpa o banco automaticamente após cada @Test
public class FinanceiroIntegrationTest {

    @Autowired
    private FinanceiroService financeiroService;

    @Autowired
    private ContaRepository contaRepository;

    @Test
    @DisplayName("Deve persistir um lançamento financeiro e validar o status automático")
    void deveSalvarFinanceiroComSucesso() {
        // ARRANGE: Criar dependência obrigatória (Conta)
        Conta conta = new Conta();
        conta.setNome("Internet");
        conta.setTipo(TipoConta.DESPESA);
        // Preencha os campos obrigatórios da sua entidade Conta aqui
        Conta contaSalva = contaRepository.save(conta);

        FinanceiroRequestDTO dto = new FinanceiroRequestDTO(
                null,
                contaSalva.getId(),
                LocalDate.now().plusDays(5), // Vencimento no futuro
                new BigDecimal("120.00"),
                "Mensalidade"
        );

        // ACT
        Financeiro resultado = financeiroService.salvar(dto);

        // ASSERT
        assertNotNull(resultado.getId(), "O banco deve gerar um ID");
        assertEquals(StatusLancamento.PENDENTE, resultado.getStatus(), "Status deve ser PENDENTE para data futura");
        assertEquals(0, new BigDecimal("120.00").compareTo(resultado.getValor()));
    }
}