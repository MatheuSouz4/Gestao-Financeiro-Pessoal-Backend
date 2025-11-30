
package com.example.loginauthapi.controllers;

import com.example.loginauthapi.domain.cadastro.Fornecedor;
import com.example.loginauthapi.dto.cadastro.FornecedorRequestDTO;
import com.example.loginauthapi.dto.cadastro.FornecedorResponseDTO;
import com.example.loginauthapi.repositories.FornecedorRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorRepository repository;

    
    @PostMapping
    @Transactional
    public ResponseEntity<FornecedorResponseDTO> cadastrarFornecedor(@RequestBody @Valid FornecedorRequestDTO data) {
        Fornecedor novoFornecedor = new Fornecedor(data);
        this.repository.save(novoFornecedor);
        return ResponseEntity.ok(new FornecedorResponseDTO(novoFornecedor));
    }

    
    @GetMapping
    public ResponseEntity<List<FornecedorResponseDTO>> consultarTodosFornecedores() {
        List<FornecedorResponseDTO> listaFornecedores = this.repository.findAll()
                .stream()
                .map(FornecedorResponseDTO::new)
                .toList();
        return ResponseEntity.ok(listaFornecedores);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> consultarFornecedorPorId(@PathVariable String id) {
        Fornecedor fornecedor = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor não encontrado"));
        return ResponseEntity.ok(new FornecedorResponseDTO(fornecedor));
    }

    
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<FornecedorResponseDTO> alterarFornecedor(@PathVariable String id, @RequestBody @Valid FornecedorRequestDTO data) {
        Fornecedor fornecedor = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor não encontrado"));

        
        fornecedor.setRazaoSocial(data.razaoSocial());
        fornecedor.setNomeFantasia(data.nomeFantasia());
        fornecedor.setCpf_Cnpj(data.cpf_Cnpj());
        fornecedor.setEmail(data.email());
        fornecedor.setTelefone(data.telefone());
        fornecedor.setEndereco(data.endereco()); 
        fornecedor.setDescricao(data.descricao());
        fornecedor.setStatus(data.status());


        return ResponseEntity.ok(new FornecedorResponseDTO(fornecedor));
    }


    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> excluirFornecedor(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Fornecedor não encontrado");
        }
        this.repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}