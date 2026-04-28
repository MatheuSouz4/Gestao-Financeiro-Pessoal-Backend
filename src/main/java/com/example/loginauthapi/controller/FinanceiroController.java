package com.example.loginauthapi.controller;

import com.example.loginauthapi.dto.DashboardResumoDTO;
import com.example.loginauthapi.dto.FinanceiroRequestDTO;
import com.example.loginauthapi.dto.QuitacaoRequestDTO;
import com.example.loginauthapi.model.Financeiro;
import com.example.loginauthapi.services.FinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/financeiro")
@CrossOrigin(origins = "*")
public class FinanceiroController {

    @Autowired
    private FinanceiroService service;

    @GetMapping
    public ResponseEntity<List<Financeiro>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Financeiro> criar(@RequestBody FinanceiroRequestDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Financeiro> atualizar(@PathVariable Long id, @RequestBody FinanceiroRequestDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @PatchMapping("/{id}/quitar")
    public ResponseEntity<Financeiro> quitar(@PathVariable Long id, @RequestBody QuitacaoRequestDTO dto) {
        return ResponseEntity.ok(service.registrarQuitacao(id, dto));
    }

    @GetMapping("/resumo")
    public ResponseEntity<DashboardResumoDTO> getResumo() {
        return ResponseEntity.ok(service.obterResumoMesAtual());
    }
}