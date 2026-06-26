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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class FinanceiroIntegrationTest {

    @Autowired
    private FinanceiroService financeiroService;

    @Autowired
    private ContaRepository contaRepository;

    @Test
    @DisplayName("Deve persistir um lançamento financeiro e validar o status automático")
    void deveSalvarFinanceiroComSucesso() {
        // ARRANGE
        Conta conta = new Conta();
        conta.setNome("Internet");
        conta.setTipo(TipoConta.DESPESA);
        Conta contaSalva = contaRepository.save(conta);

        FinanceiroRequestDTO dto = new FinanceiroRequestDTO(
                null,
                contaSalva.getId(),
                LocalDate.now().plusDays(5),
                new BigDecimal("120.00"),
                "Mensalidade",
                null,
                1,
                null
        );

        // ACT
        // Corrigido: Agora tratamos o retorno como uma lista
        List<Financeiro> resultados = financeiroService.salvar(dto);

        // ASSERT
        assertFalse(resultados.isEmpty());
        Financeiro resultado = resultados.get(0);
        assertNotNull(resultado.getId(), "O banco deve gerar um ID");
        assertEquals(StatusLancamento.PENDENTE, resultado.getStatus());
    }
}