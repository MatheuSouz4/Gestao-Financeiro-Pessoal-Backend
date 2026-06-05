package com.example.loginauthapi.controller;

import com.example.loginauthapi.model.TipoRelatorio;
import com.example.loginauthapi.services.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/csv")
    public ResponseEntity<byte[]> baixarCsv(
            @RequestParam TipoRelatorio tipo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        byte[] relatorio = relatorioService.gerarCsv(tipo, inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_" + tipo.name() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(relatorio);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> baixarPdf(
            @RequestParam TipoRelatorio tipo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        byte[] relatorio = relatorioService.gerarPdf(tipo, inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_" + tipo.name() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(relatorio);
    }
}