package com.example.loginauthapi.model;

import com.example.loginauthapi.repositories.FinanceiroRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class FinanceiroScheduler {

    @Autowired
    private FinanceiroRepository repository;

    // Roda todos os dias à meia-noite (00:00:00)
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void verificarLancamentosVencidos() {
        LocalDate hoje = LocalDate.now();

        // 1. Definimos quais status podem "vencer" (Pendente ou Parcial)
        List<StatusLancamento> statusParaVerificar = List.of(
                StatusLancamento.PENDENTE,
                StatusLancamento.PAGAMENTO_PARCIAL
        );

        // 2. Buscamos no repositório (usando o nome exato que corrigimos)
        List<Financeiro> lancamentosParaVencer = repository.findByStatusInAndDataVencimentoBefore(
                statusParaVerificar,
                hoje
        );

        // 3. Log para você acompanhar no console da IDE se ele encontrou algo
        if (!lancamentosParaVencer.isEmpty()) {
            lancamentosParaVencer.forEach(lancamento -> {
                lancamento.setStatus(StatusLancamento.VENCIDA);
            });

            repository.saveAll(lancamentosParaVencer);
            System.out.println("[Scheduler] Sucesso: " + lancamentosParaVencer.size() + " lançamentos marcados como VENCIDA.");
        } else {
            System.out.println("[Scheduler] Check: Nenhum lançamento novo vencido hoje.");
        }
    }
}