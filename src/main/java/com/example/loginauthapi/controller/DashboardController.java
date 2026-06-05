package com.example.loginauthapi.controller;

import com.example.loginauthapi.dto.DashboardResponseDTO;
import com.example.loginauthapi.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.loginauthapi.dto.ProjecaoMensalDTO;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metricas")
    public ResponseEntity<DashboardResponseDTO> getMetricas() {
        return ResponseEntity.ok(dashboardService.obterMetricasDashboard());
    }

    @GetMapping("/projecao")
    public ResponseEntity<List<ProjecaoMensalDTO>> getProjecao() {
        return ResponseEntity.ok(dashboardService.obterProjecaoAnual());
    }
}