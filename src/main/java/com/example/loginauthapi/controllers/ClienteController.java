
package com.example.loginauthapi.controllers;

import com.example.loginauthapi.domain.cadastro.Cliente;
import com.example.loginauthapi.dto.cadastro.ClienteRequestDTO;
import com.example.loginauthapi.dto.cadastro.ClienteResponseDTO;
import com.example.loginauthapi.repositories.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository repository;

    
    @PostMapping
    @Transactional 
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@RequestBody @Valid ClienteRequestDTO data) {
        Cliente novoCliente = new Cliente(data);
        this.repository.save(novoCliente);
        return ResponseEntity.ok(new ClienteResponseDTO(novoCliente));
    }

    
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> consultarTodosClientes() {
        List<ClienteResponseDTO> listaClientes = this.repository.findAll()
                .stream()
                .map(ClienteResponseDTO::new)
                .toList();
        return ResponseEntity.ok(listaClientes);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> consultarClientePorId(@PathVariable String id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        return ResponseEntity.ok(new ClienteResponseDTO(cliente));
    }

    
    @PutMapping("/{id}")
    @Transactional 
    public ResponseEntity<ClienteResponseDTO> alterarCliente(@PathVariable String id, @RequestBody @Valid ClienteRequestDTO data) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        
        cliente.setNome(data.nome());
        cliente.setCpfCnpj(data.cpfCnpj());
        cliente.setEmail(data.email());
        cliente.setTelefone(data.telefone());
        cliente.setEndereco(data.endereco());
        cliente.setDescricao(data.descricao());


        return ResponseEntity.ok(new ClienteResponseDTO(cliente));
    }


    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> excluirCliente(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado");
        }
        this.repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}