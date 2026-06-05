package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.FinanceiroRequestDTO;
import com.example.loginauthapi.model.*;
import com.example.loginauthapi.repositories.ContaRepository;
import com.example.loginauthapi.repositories.FinanceiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinanceiroService {

    @Autowired
    private FinanceiroRepository repository;

    @Autowired
    private ContaRepository contaRepository;

    // ==========================================
    // MÉTODOS DE CADASTRO E MOVIMENTAÇÃO
    // ==========================================

    public List<Financeiro> listarTodos() {
        return repository.findAll();
    }

    public List<Financeiro> buscarComFiltros(StatusLancamento status, TipoConta tipo, Long contaId, LocalDate inicio, LocalDate fim) {
        Specification<Financeiro> spec = FinanceiroSpecification.comFiltros(status, tipo, contaId, inicio, fim);
        return repository.findAll(spec);
    }

    @Transactional
    public List<Financeiro> salvar(FinanceiroRequestDTO dto) {
        Conta conta = contaRepository.findById(dto.contaId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if ("INATIVO".equals(conta.getStatus())) {
            throw new RuntimeException("Não é permitido realizar lançamentos para uma conta INATIVA.");
        }

        List<Financeiro> registrosGerados = new ArrayList<>();

        if (dto.id() != null) {
            Financeiro registro = repository.findById(dto.id())
                    .orElseThrow(() -> new RuntimeException("Lançamento não encontrado"));

            registro.setConta(conta);
            registro.setDataVencimento(dto.vencimento());
            registro.setValor(dto.valor());
            registro.setDescricao(dto.descricao());
            registro.setMotivoAlteracao(dto.motivoAlteracao());

            if (registro.getStatus() != StatusLancamento.PAGA) {
                registro.setStatus(dto.vencimento().isBefore(LocalDate.now())
                        ? StatusLancamento.VENCIDA
                        : StatusLancamento.PENDENTE);
            }
            registrosGerados.add(repository.save(registro));
            return registrosGerados;
        }

        int qtd = (dto.tipoRecorrencia() != null && dto.tipoRecorrencia() != TipoRecorrencia.NENHUMA && dto.quantidadeParcelas() != null)
                ? dto.quantidadeParcelas() : 1;

        for (int i = 0; i < qtd; i++) {
            Financeiro registro = new Financeiro();
            registro.setDataEmissao(LocalDate.now());
            registro.setConta(conta);
            registro.setValor(dto.valor());
            String sufixoParcela = qtd > 1 ? " (" + (i + 1) + "/" + qtd + ")" : "";
            registro.setDescricao(dto.descricao() + sufixoParcela);

            LocalDate vencimentoCalculado = calcularVencimento(dto.vencimento(), dto.tipoRecorrencia(), i);
            registro.setDataVencimento(vencimentoCalculado);
            registro.setStatus(vencimentoCalculado.isBefore(LocalDate.now())
                    ? StatusLancamento.VENCIDA
                    : StatusLancamento.PENDENTE);

            registrosGerados.add(repository.save(registro));
        }
        return registrosGerados;
    }

    @Transactional
    public Financeiro quitarLancamento(Long id, BigDecimal valorPago, LocalDate dataPagamento, LocalDate novaDataVencimento, MultipartFile comprovante) {
        Financeiro original = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado"));

        if (valorPago.compareTo(original.getValor()) < 0) {
            BigDecimal restante = original.getValor().subtract(valorPago);
            original.setValorPago(valorPago);
            original.setDataPagamento(dataPagamento);
            original.setStatus(StatusLancamento.PAGAMENTO_PARCIAL);

            Financeiro novoLancamento = new Financeiro();
            novoLancamento.setConta(original.getConta());
            novoLancamento.setDescricao(original.getDescricao() + " (Saldo)");
            novoLancamento.setValor(restante);
            novoLancamento.setDataEmissao(LocalDate.now());
            novoLancamento.setDataVencimento(novaDataVencimento != null ? novaDataVencimento : original.getDataVencimento());
            novoLancamento.setStatus(StatusLancamento.PENDENTE);
            novoLancamento.setIdReferencia(original.getId());

            repository.save(novoLancamento);
        } else {
            original.setValorPago(valorPago);
            original.setDataPagamento(dataPagamento);
            original.setStatus(StatusLancamento.PAGA);
        }

        Financeiro resultado = repository.save(original);
        this.atualizarSaldoConta(original.getConta().getId());
        return resultado;
    }

    @Transactional
    public Financeiro estornarLancamento(Long id, String justificativa, boolean retornarPendente) {
        Financeiro lancamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado"));

        if (lancamento.getStatus() != StatusLancamento.PAGA && lancamento.getStatus() != StatusLancamento.PAGAMENTO_PARCIAL) {
            throw new IllegalStateException("Apenas lançamentos pagos ou parciais podem ser estornados.");
        }

        if (lancamento.getStatus() == StatusLancamento.PAGAMENTO_PARCIAL) {
            List<Financeiro> residuais = repository.findByIdReferencia(lancamento.getId());
            repository.deleteAll(residuais);
        }

        if (retornarPendente) {
            lancamento.setStatus(lancamento.getDataVencimento().isBefore(LocalDate.now()) ? StatusLancamento.VENCIDA : StatusLancamento.PENDENTE);
            lancamento.setValorPago(null);
            lancamento.setDataPagamento(null);
        } else {
            lancamento.setStatus(StatusLancamento.ESTORNADA);
        }
        lancamento.setJustificativaEstorno(justificativa);

        Financeiro resultado = repository.save(lancamento);
        this.atualizarSaldoConta(lancamento.getConta().getId());
        return resultado;
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private LocalDate calcularVencimento(LocalDate dataBase, TipoRecorrencia tipo, int incremento) {
        if (tipo == null) return dataBase;
        return switch (tipo) {
            case MENSAL -> dataBase.plusMonths(incremento);
            case SEMESTRAL -> dataBase.plusMonths(6L * incremento);
            case ANUAL -> dataBase.plusYears(incremento);
            default -> dataBase;
        };
    }

    private void atualizarSaldoConta(Long contaId) {
        // Query mantida aqui apenas por necessidade de atualização de saldo da conta (escrita)
        BigDecimal saldoCalculado = repository.obterSaldoConsolidadoAteHoje(LocalDate.now(), contaId);
        Conta conta = contaRepository.findById(contaId).orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        conta.setSaldoAtual(saldoCalculado);
        contaRepository.save(conta);
    }
}