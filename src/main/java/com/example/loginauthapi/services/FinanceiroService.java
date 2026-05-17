package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.DashboardResumoDTO;
import com.example.loginauthapi.dto.FinanceiroRequestDTO;
import com.example.loginauthapi.model.*;
import com.example.loginauthapi.model.StatusLancamento;
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

    public List<Financeiro> listarTodos() {
        return repository.findAll();
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

    // Método auxiliar para organizar o cálculo de datas
    private LocalDate calcularVencimento(LocalDate dataBase, TipoRecorrencia tipo, int incremento) {
        if (tipo == null) return dataBase;

        return switch (tipo) {
            case MENSAL -> dataBase.plusMonths(incremento);
            case SEMESTRAL -> dataBase.plusMonths(6L * incremento);
            case ANUAL -> dataBase.plusYears(incremento);
            default -> dataBase;
        };
    }


    @Transactional
    public Financeiro quitarLancamento(Long id, BigDecimal valorPago, LocalDate dataPagamento, MultipartFile comprovante) {
        Financeiro original = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado"));

        BigDecimal valorOriginal = original.getValor();

        if (valorPago.compareTo(valorOriginal) < 0) {
            // LÓGICA DE PAGAMENTO PARCIAL
            BigDecimal restante = valorOriginal.subtract(valorPago);

            // 1. Atualiza o original para o valor pago e status parcial
            original.setValor(valorPago);
            original.setValorPago(valorPago);
            original.setDataPagamento(dataPagamento);
            original.setStatus(StatusLancamento.PAGAMENTO_PARCIAL);

            // 2. Cria um novo lançamento com o saldo restante
            Financeiro novoLancamento = new Financeiro();
            novoLancamento.setConta(original.getConta());
            novoLancamento.setDescricao(original.getDescricao() + " (Saldo)");
            novoLancamento.setValor(restante);
            novoLancamento.setDataEmissao(LocalDate.now());
            novoLancamento.setDataVencimento(original.getDataVencimento()); // Ou data sugerida pelo usuário
            novoLancamento.setStatus(StatusLancamento.PENDENTE);
            novoLancamento.setIdReferencia(original.getId()); // Referência ao ID anterior

            repository.save(novoLancamento);
        } else {
            // LÓGICA DE QUITAÇÃO TOTAL
            original.setValorPago(valorPago);
            original.setDataPagamento(dataPagamento);
            original.setStatus(StatusLancamento.PAGA);
        }

        // Salvar comprovante se houver (sua lógica de upload aqui)
        return repository.save(original);
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

    public List<Financeiro> buscarComFiltros(StatusLancamento status, TipoConta tipo, Long contaId, LocalDate inicio, LocalDate fim) {
        Specification<Financeiro> spec = FinanceiroSpecification.comFiltros(status, tipo, contaId, inicio, fim);
        return repository.findAll(spec);
    }
}