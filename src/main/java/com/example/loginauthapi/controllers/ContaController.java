package com.example.loginauthapi.controllers;

import com.example.loginauthapi.domain.cadastro.Conta;
import com.example.loginauthapi.domain.cadastro.TipoConta;
import com.example.loginauthapi.dto.cadastro.ContaRequestDTO;
import com.example.loginauthapi.dto.cadastro.ContaResponseDTO;
import com.example.loginauthapi.repositories.ContaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaRepository repository;


    @PostMapping
    @Transactional
    public ResponseEntity<ContaResponseDTO> cadastrarConta(@RequestBody @Valid ContaRequestDTO data) {
        // 🚨 Validação extra se necessário, garantindo que apenas um ID de relacionamento é enviado
        if (data.tipo() == TipoConta.RECEITA && (data.clienteId() == null || data.clienteId().isBlank())) {
            return ResponseEntity.badRequest().build(); // Cliente ID é obrigatório para RECEITA
        }
        if (data.tipo() == TipoConta.DESPESA && (data.fornecedorId() == null || data.fornecedorId().isBlank())) {
            return ResponseEntity.badRequest().build(); // Fornecedor ID é obrigatório para DESPESA
        }

        Conta novaConta = new Conta(data);
        this.repository.save(novaConta);
        return ResponseEntity.ok(new ContaResponseDTO(novaConta));
    }


    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> consultarTodasContas() {
        List<ContaResponseDTO> listaContas = this.repository.findAll()
                .stream()
                .map(ContaResponseDTO::new)
                .toList();
        return ResponseEntity.ok(listaContas);
    }


    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ContaResponseDTO> alterarConta(@PathVariable String id, @RequestBody @Valid ContaRequestDTO data) {
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta base não encontrada"));

        // Atualização dos campos
        conta.setNome(data.nome());
        conta.setTipo(data.tipo());
        conta.setRecorrencia(data.recorrencia());
        conta.setDescricao(data.descricao());
        conta.setStatus(data.status());

        // 🚨 Atualização condicional do relacionamento
        if (data.tipo() == TipoConta.RECEITA) {
            conta.setCliente(data.clienteId());
            conta.setFornecedor(null);
        } else if (data.tipo() == TipoConta.DESPESA) {
            conta.setFornecedor(data.fornecedorId());
            conta.setCliente(null);
        }

        return ResponseEntity.ok(new ContaResponseDTO(conta));
    }


    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> excluirConta(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Conta base não encontrada");
        }
        this.repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}