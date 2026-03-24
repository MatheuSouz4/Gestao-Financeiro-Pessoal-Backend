package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.DashboardResumoDTO;
import com.example.loginauthapi.dto.LancamentoRequestDTO;
import com.example.loginauthapi.dto.QuitacaoRequestDTO;
import com.example.loginauthapi.model.Conta;
import com.example.loginauthapi.model.Lancamento;
import com.example.loginauthapi.model.StatusLancamento;
import com.example.loginauthapi.repositories.ContaRepository;
import com.example.loginauthapi.repositories.LancamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class LancamentoService {

    @Autowired
    private LancamentoRepository repository;

    @Autowired
    private ContaRepository contaRepository; // Assumindo que você já tem

    public List<Lancamento> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Lancamento salvar(LancamentoRequestDTO dto) {
        Conta conta = contaRepository.findById(dto.contaId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        Lancamento lancamento = new Lancamento();
        if (dto.id() != null) lancamento.setId(dto.id());

        lancamento.setConta(conta);
        lancamento.setDataEmissao(LocalDate.now());
        lancamento.setDataVencimento(dto.vencimento());
        lancamento.setValor(dto.valor());
        lancamento.setDescricao(dto.descricao());

        // Lógica de status inicial
        lancamento.setStatus(dto.vencimento().isBefore(LocalDate.now())
                ? StatusLancamento.VENCIDA : StatusLancamento.PENDENTE);

        return repository.save(lancamento);
    }

    @Transactional
    public Lancamento registrarQuitacao(Long id, QuitacaoRequestDTO dto) {
        Lancamento lancamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado"));

        lancamento.setDataPagamento(dto.dataPagamento());
        lancamento.setValorPago(dto.valorPago());
        lancamento.setComprovanteUrl(dto.comprovanteUrl());
        lancamento.setStatus(StatusLancamento.PAGA);

        return repository.save(lancamento);
    }

    public DashboardResumoDTO obterResumoMesAtual() {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fim = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<Lancamento> lancamentosMes = repository.findAll().stream()
                .filter(l -> !l.getDataVencimento().isBefore(inicio) && !l.getDataVencimento().isAfter(fim))
                .toList();

        BigDecimal pago = lancamentosMes.stream()
                .filter(l -> l.getStatus() == StatusLancamento.PAGA)
                .map(l -> l.getValorPago() != null ? l.getValorPago() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendente = lancamentosMes.stream()
                .filter(l -> l.getStatus() != StatusLancamento.PAGA)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long qtd = lancamentosMes.stream()
                .filter(l -> l.getStatus() != StatusLancamento.PAGA)
                .count();

        return new DashboardResumoDTO(pago, pendente, qtd);
    }
}
