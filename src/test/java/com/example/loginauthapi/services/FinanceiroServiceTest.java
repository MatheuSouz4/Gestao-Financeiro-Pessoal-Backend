package com.example.loginauthapi.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.loginauthapi.dto.FinanceiroRequestDTO;
import com.example.loginauthapi.model.Conta;
import com.example.loginauthapi.model.Financeiro;
import com.example.loginauthapi.model.StatusLancamento;
import com.example.loginauthapi.repositories.ContaRepository;
import com.example.loginauthapi.repositories.FinanceiroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class FinanceiroServiceTest {

    @Mock
    private FinanceiroRepository repository;

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private FinanceiroService service;

    @Test
    @DisplayName("Deve salvar um lançamento com status VENCIDA quando a data for retroativa")
    void deveSalvarComStatusVencido() {
        // ARRANGE
        FinanceiroRequestDTO dto = new FinanceiroRequestDTO(
                null, 1L, LocalDate.now().minusDays(10), new BigDecimal("250.00"), "Aluguel"
        );
        Conta contaMock = new Conta();

        when(contaRepository.findById(1L)).thenReturn(Optional.of(contaMock));
        when(repository.save(any(Financeiro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Financeiro resultado = service.salvar(dto);

        // ASSERT
        assertEquals(StatusLancamento.VENCIDA, resultado.getStatus());
        verify(repository, times(1)).save(any());
    }
}