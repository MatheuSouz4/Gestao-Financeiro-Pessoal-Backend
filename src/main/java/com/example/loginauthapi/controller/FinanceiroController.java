package com.example.loginauthapi.controller;

import com.example.loginauthapi.dto.GraficoDashboardDTO;
import com.example.loginauthapi.dto.ResumoDashboardDTO;
import com.example.loginauthapi.dto.FinanceiroRequestDTO;
import com.example.loginauthapi.model.Financeiro;
import com.example.loginauthapi.model.FinanceiroSpecification;
import com.example.loginauthapi.model.StatusLancamento;
import com.example.loginauthapi.model.TipoConta;
import com.example.loginauthapi.services.FinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/financeiro")
@CrossOrigin(origins = "*")
public class FinanceiroController {

    @Autowired
    private FinanceiroService service;

    // 1. Listar todos os lançamentos
    @GetMapping
    public ResponseEntity<List<Financeiro>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // 2. Criar novo lançamento (com ou sem recorrência)
    @PostMapping
    public ResponseEntity<List<Financeiro>> criar(@RequestBody FinanceiroRequestDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    // 3. Atualizar um lançamento existente
    @PutMapping("/{id}")
    public ResponseEntity<List<Financeiro>> atualizar(@PathVariable Long id, @RequestBody FinanceiroRequestDTO dto) {
        FinanceiroRequestDTO dtoComId = new FinanceiroRequestDTO(
                id,
                dto.contaId(),
                dto.vencimento(),
                dto.valor(),
                dto.descricao(),
                dto.tipoRecorrencia(),
                dto.quantidadeParcelas(),
                dto.motivoAlteracao()
        );
        return ResponseEntity.ok(service.salvar(dtoComId));
    }

    // 4. Quitar lançamento
    @PatchMapping(value = "/{id}/quitar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Financeiro> quitar(
            @PathVariable Long id,
            @RequestParam("dataPagamento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamento,
            @RequestParam("valorPago") BigDecimal valorPago,
            @RequestParam(value = "novaDataVencimento", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate novaDataVencimento,
            @RequestParam(value = "comprovante", required = false) MultipartFile comprovante) {

        return ResponseEntity.ok(service.quitarLancamento(id, valorPago, dataPagamento, novaDataVencimento, comprovante));
    }

    // 5. Estornar lançamento
    @PatchMapping("/{id}/estornar")
    public ResponseEntity<Financeiro> estornar(@PathVariable Long id, @RequestBody java.util.Map<String, Object> payload) {
        String justificativa = (String) payload.get("justificativaEstorno");
        Boolean retornarPendente = (Boolean) payload.get("retornarPendente");

        // Proteção contra nulos
        if (retornarPendente == null) retornarPendente = false;

        return ResponseEntity.ok(service.estornarLancamento(id,justificativa, retornarPendente));
    }

    // 6. Filtrar lançamentos
    @GetMapping("/filtro")
    public ResponseEntity<List<Financeiro>> filtrar(
            @RequestParam(required = false) StatusLancamento status,
            @RequestParam(required = false) TipoConta tipo,
            @RequestParam(required = false) Long contaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        return ResponseEntity.ok(service.buscarComFiltros(status, tipo, contaId, inicio, fim));
    }

    // ==========================================
    // ENDPOINTS DO DASHBOARD
    // ==========================================

    @GetMapping("/resumo")
    public ResponseEntity<ResumoDashboardDTO> getResumo() {
        // Atualizado para chamar o novo método centralizado
        return ResponseEntity.ok(service.obterResumoFinanceiro());
    }

    @GetMapping("/grafico")
    public ResponseEntity<List<GraficoDashboardDTO>> getGrafico() {
        // Novo endpoint responsável por alimentar a linha de fluxo de caixa no front-end
        return ResponseEntity.ok(service.obterDadosGrafico());
    }
}