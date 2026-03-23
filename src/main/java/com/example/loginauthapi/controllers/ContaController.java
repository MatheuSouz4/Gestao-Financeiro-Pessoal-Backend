package com.example.loginauthapi.controllers;

import com.example.loginauthapi.dto.ContaRequestDTO;
import com.example.loginauthapi.dto.ContaResponseDTO;
import com.example.loginauthapi.services.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContaController {

    private final ContaService service;

    @PostMapping
    public ResponseEntity<ContaResponseDTO> cadastrarConta(@RequestBody @Valid ContaRequestDTO data) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(data));
        } catch (IllegalArgumentException e) {
            // Captura o erro da validação e retorna 400 Bad Request amigável
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> consultarTodasContas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> consultarContaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> alterarConta(@PathVariable Long id, @RequestBody @Valid ContaRequestDTO data) {
        try {
            return ResponseEntity.ok(service.atualizar(id, data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirConta(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}