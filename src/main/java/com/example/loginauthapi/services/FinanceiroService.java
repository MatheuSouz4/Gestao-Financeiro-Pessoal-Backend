package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.DashboardResumoDTO;
import com.example.loginauthapi.dto.FinanceiroRequestDTO;
import com.example.loginauthapi.dto.QuitacaoRequestDTO;
import com.example.loginauthapi.model.Conta;
import com.example.loginauthapi.model.Financeiro;
import com.example.loginauthapi.model.StatusLancamento;
import com.example.loginauthapi.repositories.ContaRepository;
import com.example.loginauthapi.repositories.FinanceiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinanceiroService {

    @Autowired
    private FinanceiroRepository repository;

    @Autowired
    private ContaRepository contaRepository;

    public List<Financeiro> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Financeiro salvar(FinanceiroRequestDTO dto) {
        Conta conta = contaRepository.findById(dto.contaId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        Financeiro registro;
        if (dto.id() != null) {
            registro = repository.findById(dto.id()).orElse(new Financeiro());
        } else {
            registro = new Financeiro();
            registro.setDataEmissao(LocalDate.now());
        }

        registro.setConta(conta);
        registro.setDataVencimento(dto.vencimento());
        registro.setValor(dto.valor());
        registro.setDescricao(dto.descricao());

        // Só altera status se não estiver pago
        if (registro.getStatus() != StatusLancamento.PAGA) {
            registro.setStatus(dto.vencimento().isBefore(LocalDate.now())
                    ? StatusLancamento.VENCIDA : StatusLancamento.PENDENTE);
        }

        return repository.save(registro);
    }

    @Transactional
    public Financeiro registrarQuitacao(Long id, QuitacaoRequestDTO dto) {
        Financeiro registro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro financeiro não encontrado"));

        registro.setDataPagamento(dto.dataPagamento());
        registro.setValorPago(dto.valorPago());
        registro.setComprovanteUrl(dto.comprovanteUrl());
        registro.setStatus(StatusLancamento.PAGA);

        return repository.save(registro);
    }

    public DashboardResumoDTO obterResumoMesAtual() {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fim = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        // Idealmente, usar uma Query customizada no Repository para performance
        List<Financeiro> registrosMes = repository.findAll().stream()
                .filter(f -> !f.getDataVencimento().isBefore(inicio) && !f.getDataVencimento().isAfter(fim))
                .toList();

        BigDecimal receitasPagas = registrosMes.stream()
                .filter(f -> f.getTipo().equals("RECEITA") && f.getStatus() == StatusLancamento.PAGA)
                .map(Financeiro::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal despesasPagas = registrosMes.stream()
                .filter(f -> f.getTipo().equals("DESPESA") && f.getStatus() == StatusLancamento.PAGA)
                .map(Financeiro::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendentes = registrosMes.stream()
                .filter(f -> f.getStatus() != StatusLancamento.PAGA)
                .count();

        return new DashboardResumoDTO(receitasPagas, despesasPagas, pendentes);
    }
}