package com.example.loginauthapi.controllers;

import com.example.loginauthapi.dto.FornecedorRequestDTO;
import com.example.loginauthapi.dto.FornecedorResponseDTO;
import com.example.loginauthapi.services.FornecedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fornecedores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Essencial para o Angular conseguir fazer as requisições sem erro de CORS
public class FornecedorController {

    private final FornecedorService service;

    @GetMapping
    public ResponseEntity<List<FornecedorResponseDTO>> consultarTodosFornecedores() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> consultarFornecedorPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FornecedorResponseDTO> cadastrarFornecedor(@RequestBody @Valid FornecedorRequestDTO data) {
        // Retornando 201 Created para post, o que é o padrão correto de API REST
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> alterarFornecedor(@PathVariable Long id, @RequestBody @Valid FornecedorRequestDTO data) {
        return ResponseEntity.ok(service.atualizar(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirFornecedor(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build(); // Retornando 204 No Content
    }
}