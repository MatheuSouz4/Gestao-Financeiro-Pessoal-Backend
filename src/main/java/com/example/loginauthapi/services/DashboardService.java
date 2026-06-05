package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.DashboardProjection;
import com.example.loginauthapi.dto.DashboardResponseDTO;
import com.example.loginauthapi.repositories.FinanceiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.loginauthapi.dto.ProjecaoMensalDTO;
import com.example.loginauthapi.model.Financeiro;
import com.example.loginauthapi.model.StatusLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinanceiroRepository financeiroRepository;

    @Transactional(readOnly = true)
    public DashboardResponseDTO obterMetricasDashboard() {
        DashboardProjection proj = financeiroRepository.obterResumoDashboardGeral();

        // Passamos os dados crus para o DTO, que faz as matemáticas de Saldo no construtor
        return new DashboardResponseDTO(
                proj.getReceitasRecebidas(),
                proj.getReceitasPendentes(),
                proj.getReceitasVencidas(),
                proj.getDespesasPagas(),
                proj.getDespesasPendentes(),
                proj.getDespesasVencidas()
        );
    }

    @Transactional(readOnly = true)
    public List<ProjecaoMensalDTO> obterProjecaoAnual() {
        int anoAtual = LocalDate.now().getYear();
        LocalDate inicioAno = LocalDate.of(anoAtual, 1, 1);
        LocalDate fimAno = LocalDate.of(anoAtual, 12, 31);

        List<Financeiro> lancamentos = financeiroRepository.findByDataVencimentoBetween(inicioAno, fimAno);

        // Inicializa o mapa para os 12 meses com valor ZERO
        Map<Integer, BigDecimal> receitasPorMes = new HashMap<>();
        Map<Integer, BigDecimal> despesasPorMes = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            receitasPorMes.put(i, BigDecimal.ZERO);
            despesasPorMes.put(i, BigDecimal.ZERO);
        }

        // Agrupa os valores
        for (Financeiro f : lancamentos) {
            if (f.getDataVencimento() == null || f.getConta() == null) continue;

            int mes = f.getDataVencimento().getMonthValue();

            // Prioriza o valor pago se houver, senão usa o valor original
            BigDecimal valor = (f.getStatus() == StatusLancamento.PAGA || f.getStatus() == StatusLancamento.PAGAMENTO_PARCIAL) && f.getValorPago() != null
                    ? f.getValorPago() : f.getValor();

            String tipoConta = f.getConta().getTipo().name();

            if ("RECEITA".equals(tipoConta)) {
                receitasPorMes.put(mes, receitasPorMes.get(mes).add(valor));
            } else if ("DESPESA".equals(tipoConta)) {
                despesasPorMes.put(mes, despesasPorMes.get(mes).add(valor));
            }
        }

        // Formata a saída
        String[] nomesMeses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        List<ProjecaoMensalDTO> projecao = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            projecao.add(new ProjecaoMensalDTO(nomesMeses[i - 1], receitasPorMes.get(i), despesasPorMes.get(i)));
        }

        return projecao;
    }
}