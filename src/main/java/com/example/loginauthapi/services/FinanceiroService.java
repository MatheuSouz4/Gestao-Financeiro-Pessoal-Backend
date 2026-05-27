package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.GraficoDashboardDTO;
import com.example.loginauthapi.dto.ResumoDashboardDTO;
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
        // 1. Busca a conta e valida existência
        Conta conta = contaRepository.findById(dto.contaId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        // 2. Validação de Integridade: Impede uso de contas inativas
        if ("INATIVO".equals(conta.getStatus())) {
            throw new RuntimeException("Não é permitido realizar lançamentos para uma conta INATIVA.");
        }

        List<Financeiro> registrosGerados = new ArrayList<>();

        // 3. Lógica de Atualização (Registro Individual)
        if (dto.id() != null) {
            Financeiro registro = repository.findById(dto.id())
                    .orElseThrow(() -> new RuntimeException("Lançamento não encontrado"));

            registro.setConta(conta);
            registro.setDataVencimento(dto.vencimento());
            registro.setValor(dto.valor());
            registro.setDescricao(dto.descricao());
            registro.setMotivoAlteracao(dto.motivoAlteracao());

            // Atualiza status se não estiver pago
            if (registro.getStatus() != StatusLancamento.PAGA) {
                registro.setStatus(dto.vencimento().isBefore(LocalDate.now())
                        ? StatusLancamento.VENCIDA
                        : StatusLancamento.PENDENTE);
            }

            registrosGerados.add(repository.save(registro));
            return registrosGerados;
        }

        // 4. Lógica de Criação com Recorrência
        int qtd = (dto.tipoRecorrencia() != null && dto.tipoRecorrencia() != TipoRecorrencia.NENHUMA && dto.quantidadeParcelas() != null)
                ? dto.quantidadeParcelas() : 1;

        for (int i = 0; i < qtd; i++) {
            Financeiro registro = new Financeiro();
            registro.setDataEmissao(LocalDate.now());
            registro.setConta(conta);
            registro.setValor(dto.valor());

            // Adiciona a marcação de parcelas
            String sufixoParcela = qtd > 1 ? " (" + (i + 1) + "/" + qtd + ")" : "";
            registro.setDescricao(dto.descricao() + sufixoParcela);

            // Cálculo de Vencimento Dinâmico
            LocalDate vencimentoCalculado = calcularVencimento(dto.vencimento(), dto.tipoRecorrencia(), i);

            registro.setDataVencimento(vencimentoCalculado);

            // Define status inicial baseado na data atual
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
        BigDecimal valorOriginal = original.getValor();

        if (valorPago.compareTo(valorOriginal) < 0) {
            // LÓGICA DE PAGAMENTO PARCIAL
            BigDecimal restante = valorOriginal.subtract(valorPago);

            original.setValorPago(valorPago);
            original.setDataPagamento(dataPagamento);
            original.setStatus(StatusLancamento.PAGAMENTO_PARCIAL);

            Financeiro novoLancamento = new Financeiro();
            novoLancamento.setConta(original.getConta());
            novoLancamento.setDescricao(original.getDescricao() + " (Saldo)");
            novoLancamento.setValor(restante);
            novoLancamento.setDataEmissao(LocalDate.now());

            if (novaDataVencimento != null) {
                novoLancamento.setDataVencimento(novaDataVencimento);
            } else {
                novoLancamento.setDataVencimento(original.getDataVencimento());
            }

            novoLancamento.setStatus(StatusLancamento.PENDENTE);
            novoLancamento.setIdReferencia(original.getId());

            repository.save(novoLancamento);
        } else {
            // LÓGICA DE QUITAÇÃO TOTAL
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

        if (lancamento.getStatus() != StatusLancamento.PAGA &&
                lancamento.getStatus() != StatusLancamento.PAGAMENTO_PARCIAL) {
            throw new IllegalStateException("Apenas lançamentos pagos ou parciais podem ser estornados.");
        }

        Long contaId = lancamento.getConta().getId();

        // Limpa os residuais se o pagamento original foi parcial
        if (lancamento.getStatus() == StatusLancamento.PAGAMENTO_PARCIAL) {
            List<Financeiro> residuais = repository.findByIdReferencia(lancamento.getId());
            if (!residuais.isEmpty()) {
                repository.deleteAll(residuais);
            }
        }

        if (retornarPendente) {
            // Opção 1: Desfazer Pagamento
            // Verifica se a data original já venceu para aplicar o status correto
            if (lancamento.getDataVencimento().isBefore(LocalDate.now())) {
                lancamento.setStatus(StatusLancamento.VENCIDA);
            } else {
                lancamento.setStatus(StatusLancamento.PENDENTE);
            }
            // Como o pagamento foi "desfeito", limpamos os rastros
            lancamento.setValorPago(null);
            lancamento.setDataPagamento(null);
        } else {
            // Opção 2: Estorno definitivo
            lancamento.setStatus(StatusLancamento.ESTORNADA);
        }

        lancamento.setJustificativaEstorno(justificativa);

        Financeiro resultado = repository.save(lancamento);
        this.atualizarSaldoConta(contaId);

        return resultado;
    }

    // ==========================================
    // MÉTODOS DO DASHBOARD (INTEGRADOS)
    // ==========================================

    public ResumoDashboardDTO obterResumoFinanceiro() {
        // Usa as consultas JPQL para garantir cálculos rápidos pelo banco
        BigDecimal recPagas = tratarNulo(repository.sumReceitasRecebidas());
        BigDecimal recPend = tratarNulo(repository.sumReceitasPendentes());
        BigDecimal despPagas = tratarNulo(repository.sumDespesasPagas());
        BigDecimal despPend = tratarNulo(repository.sumDespesasPendentes());

        BigDecimal totalPago = recPagas.subtract(despPagas);
        BigDecimal totalPendente = recPend.subtract(despPend);
        Long qtdPendentes = repository.countPendentes();

        return new ResumoDashboardDTO(
                recPagas, recPend, despPagas, despPend,
                totalPago, totalPendente, qtdPendentes
        );
    }

    public List<GraficoDashboardDTO> obterDadosGrafico() {
        return repository.buscarDadosGrafico(LocalDate.now().minusDays(30));
    }

    public BigDecimal calcularSaldoConsolidado(LocalDate inicio, LocalDate fim, Long contaId) {
        if (inicio == null && fim == null) {
            return contaRepository.obterSaldoConsolidadoSemFiltroData(contaId);
        }
        return contaRepository.obterSaldoConsolidadoComFiltro(inicio, fim, contaId);
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
        BigDecimal totalReceitas = tratarNulo(repository.sumReceitasRecebidasByConta(contaId));
        BigDecimal totalDespesas = tratarNulo(repository.sumDespesasPagasByConta(contaId));

        BigDecimal saldoCalculado = totalReceitas.subtract(totalDespesas);

        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        conta.setSaldoAtual(saldoCalculado);
        contaRepository.save(conta);
    }

    // Evita NullPointerException caso o repositório retorne null em somas vazias
    private BigDecimal tratarNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}