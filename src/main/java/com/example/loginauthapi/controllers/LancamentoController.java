package com.example.loginauthapi.controllers;

import com.example.loginauthapi.dto.DashboardResumoDTO;
import com.example.loginauthapi.dto.LancamentoGraficoDTO;
import com.example.loginauthapi.dto.LancamentoRequestDTO;
import com.example.loginauthapi.dto.QuitacaoRequestDTO;
import com.example.loginauthapi.model.Lancamento;
import com.example.loginauthapi.services.LancamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {

    @Autowired
    private LancamentoService service;

    @GetMapping
    public ResponseEntity<List<Lancamento>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Lancamento> criar(@RequestBody LancamentoRequestDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lancamento> atualizar(@PathVariable Long id, @RequestBody LancamentoRequestDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<Lancamento> quitar(@PathVariable Long id, @RequestBody QuitacaoRequestDTO dto) {
        return ResponseEntity.ok(service.registrarQuitacao(id, dto));
    }

    @GetMapping("/resumo")
    public ResponseEntity<DashboardResumoDTO> getResumo() {
        return ResponseEntity.ok(service.obterResumoMesAtual());
    }

    @GetMapping("/grafico")
    public List<LancamentoGraficoDTO> getDadosGrafico() {
        return service.obterDadosGrafico(LocalDate.now().minusDays(30)); // Últimos 30 dias
    }
}