package com.example.loginauthapi.controller;

import com.example.loginauthapi.dto.ContaRequestDTO;
import com.example.loginauthapi.dto.ContaResponseDTO;
import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoConta;
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
}