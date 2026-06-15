package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.DashboardProjection;
import com.example.loginauthapi.dto.DashboardResponseDTO;
import com.example.loginauthapi.dto.ProjecaoMensalDTO;
import com.example.loginauthapi.dto.TopCategoriaDTO;
import com.example.loginauthapi.model.Financeiro;
import com.example.loginauthapi.model.StatusLancamento;
import com.example.loginauthapi.repositories.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    @Transactional(readOnly = true)
    public DashboardResponseDTO obterMetricasDashboard(LocalDate inicio, LocalDate fim) {
        DashboardProjection dados = dashboardRepository.obterResumoDashboardGeralComFiltro(inicio, fim);

        return new DashboardResponseDTO(
                dados.getReceitasRecebidas(),
                dados.getReceitasPendentes(),
                dados.getReceitasVencidas(),
                dados.getDespesasPagas(),
                dados.getDespesasPendentes(),
                dados.getDespesasVencidas()
        );
    }

    @Transactional(readOnly = true)
    public List<TopCategoriaDTO> obterTopReceitas(LocalDate inicio, LocalDate fim) {
        return dashboardRepository.obterTopReceitas(inicio, fim, PageRequest.of(0, 5));
    }

    @Transactional(readOnly = true)
    public List<TopCategoriaDTO> obterTopDespesas(LocalDate inicio, LocalDate fim) {
        return dashboardRepository.obterTopDespesas(inicio, fim, PageRequest.of(0, 5));
    }

    @Transactional(readOnly = true)
    public List<ProjecaoMensalDTO> obterProjecao(LocalDate inicio, LocalDate fim) {
        List<Financeiro> lancamentos = dashboardRepository.findByDataVencimentoBetweenFiltro(inicio, fim);

        // Mapa ordenado para garantir que a linha do tempo flua corretamente
        Map<YearMonth, BigDecimal> receitasPorMes = new TreeMap<>();
        Map<YearMonth, BigDecimal> despesasPorMes = new TreeMap<>();

        for (Financeiro f : lancamentos) {
            if (f.getDataVencimento() == null || f.getConta() == null) continue;

            YearMonth mesAno = YearMonth.from(f.getDataVencimento());
            BigDecimal valor = (f.getStatus() == StatusLancamento.PAGA || f.getStatus() == StatusLancamento.PAGAMENTO_PARCIAL) && f.getValorPago() != null
                    ? f.getValorPago() : f.getValor();

            if ("RECEITA".equals(f.getConta().getTipo().name())) {
                receitasPorMes.merge(mesAno, valor, BigDecimal::add);
            } else if ("DESPESA".equals(f.getConta().getTipo().name())) {
                despesasPorMes.merge(mesAno, valor, BigDecimal::add);
            }
        }

        // Unificar as chaves (meses que têm apenas despesa ou apenas receita)
        Set<YearMonth> todosOsMeses = new TreeSet<>();
        todosOsMeses.addAll(receitasPorMes.keySet());
        todosOsMeses.addAll(despesasPorMes.keySet());

        List<ProjecaoMensalDTO> projecao = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM/yy");

        for (YearMonth ym : todosOsMeses) {
            String mesFormatado = ym.format(formatter);
            BigDecimal totalReceitas = receitasPorMes.getOrDefault(ym, BigDecimal.ZERO);
            BigDecimal totalDespesas = despesasPorMes.getOrDefault(ym, BigDecimal.ZERO);
            projecao.add(new ProjecaoMensalDTO(mesFormatado, totalReceitas, totalDespesas));
        }

        return projecao;
    }
}