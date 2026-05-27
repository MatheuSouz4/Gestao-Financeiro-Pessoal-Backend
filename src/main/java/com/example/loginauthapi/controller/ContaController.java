package com.example.loginauthapi.controller;

import com.example.loginauthapi.dto.ContaRequestDTO;
import com.example.loginauthapi.dto.ContaResponseDTO;
import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoConta;
import com.example.loginauthapi.services.ContaService;
import com.example.loginauthapi.services.FinanceiroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContaController {

    private final ContaService service;
    private final FinanceiroService financeiroService; // Injetado para prover os dados do dashboard

    @PostMapping
    public ResponseEntity<ContaResponseDTO> cadastrar(@RequestBody @Valid ContaRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(data));
    }

    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> listar(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) TipoConta tipo
    ) {
        return ResponseEntity.ok(service.listarComFiltros(status, tipo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> alterar(@PathVariable Long id, @RequestBody @Valid ContaRequestDTO data) {
        return ResponseEntity.ok(service.atualizar(id, data));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    // ENDPOINT: GET /contas/{id}/saldo
    @GetMapping("/{id}/saldo")
    public ResponseEntity<BigDecimal> obterSaldoAtual(@PathVariable Long id) {
        return ResponseEntity.ok(service.obterSaldoAtual(id));
    }

    // ENDPOINT ATUALIZADO: GET /contas/saldo-total (Com suporte aos filtros do dashboard)
    @GetMapping("/saldo-total")
    public ResponseEntity<BigDecimal> obterSaldoConsolidado(
            @RequestParam(value = "inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(value = "fim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(value = "contaId", required = false) Long contaId) {

        // Delega para o FinanceiroService que centraliza as métricas do painel
        BigDecimal saldo = financeiroService.calcularSaldoConsolidado(inicio, fim, contaId);
        return ResponseEntity.ok(saldo);
    }
}