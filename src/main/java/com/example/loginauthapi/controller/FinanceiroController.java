package com.example.loginauthapi.controller;

import com.example.loginauthapi.dto.FinanceiroRequestDTO;
import com.example.loginauthapi.model.Financeiro;
import com.example.loginauthapi.model.StatusLancamento;
import com.example.loginauthapi.model.TipoConta;
import com.example.loginauthapi.services.FinanceiroService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/financeiro")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FinanceiroController {

    private final FinanceiroService service;

    @GetMapping
    public ResponseEntity<List<Financeiro>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    public ResponseEntity<List<Financeiro>> criar(@RequestBody FinanceiroRequestDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<List<Financeiro>> atualizar(@PathVariable Long id, @RequestBody FinanceiroRequestDTO dto) {
        FinanceiroRequestDTO dtoComId = new FinanceiroRequestDTO(
                id, dto.contaId(), dto.vencimento(), dto.valor(), dto.descricao(),
                dto.tipoRecorrencia(), dto.quantidadeParcelas(), dto.motivoAlteracao(), dto.justificativaEstorno()
        );
        return ResponseEntity.ok(service.salvar(dtoComId));
    }

    @PatchMapping(value = "/{id}/quitar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Financeiro> quitar(
            @PathVariable Long id,
            @RequestParam("dataPagamento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamento,
            @RequestParam("valorPago") BigDecimal valorPago,
            @RequestParam(value = "novaDataVencimento", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate novaDataVencimento,
            @RequestParam(value = "comprovante", required = false) MultipartFile comprovante) {
        return ResponseEntity.ok(service.quitarLancamento(id, valorPago, dataPagamento, novaDataVencimento, comprovante));
    }

    @PatchMapping("/{id}/estornar")
    public ResponseEntity<Financeiro> estornar(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        String justificativa = (String) payload.get("justificativaEstorno");
        Boolean retornarPendente = (Boolean) payload.get("retornarPendente");
        if (retornarPendente == null) retornarPendente = false;
        return ResponseEntity.ok(service.estornarLancamento(id, justificativa, retornarPendente));
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<Financeiro>> filtrar(
            @RequestParam(required = false) StatusLancamento status,
            @RequestParam(required = false) TipoConta tipo,
            @RequestParam(required = false) Long contaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.buscarComFiltros(status, tipo, contaId, inicio, fim));
    }
}