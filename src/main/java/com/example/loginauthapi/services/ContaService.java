package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.ContaRequestDTO;
import com.example.loginauthapi.dto.ContaResponseDTO;
import com.example.loginauthapi.model.Conta;
import com.example.loginauthapi.model.TipoConta;
import com.example.loginauthapi.repositories.ContaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository repository;

    public List<ContaResponseDTO> listarTodas() {
        return repository.findAll().stream()
                .map(ContaResponseDTO::new)
                .toList();
    }

    public ContaResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(ContaResponseDTO::new)
                .orElseThrow(() -> new EntityNotFoundException("Conta ID " + id + " não encontrada"));
    }

    @Transactional
    public ContaResponseDTO salvar(ContaRequestDTO data) {
        validarVinculoObrigatorio(data);
        var novaConta = new Conta(data);
        return new ContaResponseDTO(repository.save(novaConta));
    }

    @Transactional
    public ContaResponseDTO atualizar(Long id, ContaRequestDTO data) {
        validarVinculoObrigatorio(data);

        Conta conta = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        conta.atualizarDados(data);
        return new ContaResponseDTO(repository.save(conta));
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Não é possível excluir: Conta não encontrada");
        }
        repository.deleteById(id);
    }

    private void validarVinculoObrigatorio(ContaRequestDTO data) {
        if (data.tipo() == TipoConta.RECEITA && data.clienteId() == null) {
            throw new IllegalArgumentException("Receitas exigem um Cliente vinculado.");
        }
        if (data.tipo() == TipoConta.DESPESA && data.fornecedorId() == null) {
            throw new IllegalArgumentException("Despesas exigem um Fornecedor vinculado.");
        }
    }
}