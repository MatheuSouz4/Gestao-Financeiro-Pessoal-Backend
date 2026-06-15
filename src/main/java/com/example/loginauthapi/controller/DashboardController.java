package com.example.loginauthapi.controller;

import com.example.loginauthapi.dto.DashboardResponseDTO;
import com.example.loginauthapi.dto.ProjecaoMensalDTO;
import com.example.loginauthapi.dto.TopCategoriaDTO;
import com.example.loginauthapi.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metricas")
    public ResponseEntity<DashboardResponseDTO> getMetricas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(dashboardService.obterMetricasDashboard(inicio, fim));
    }

    @GetMapping("/projecao")
    public ResponseEntity<List<ProjecaoMensalDTO>> getProjecao(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(dashboardService.obterProjecao(inicio, fim));
    }

    @GetMapping("/top-receitas")
    public ResponseEntity<List<TopCategoriaDTO>> getTopReceitas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(dashboardService.obterTopReceitas(inicio, fim));
    }

    @GetMapping("/top-despesas")
    public ResponseEntity<List<TopCategoriaDTO>> getTopDespesas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(dashboardService.obterTopDespesas(inicio, fim));
    }
}