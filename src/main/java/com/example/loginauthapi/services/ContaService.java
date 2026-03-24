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
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));
        return new ContaResponseDTO(conta);
    }

    @Transactional
    public ContaResponseDTO salvar(ContaRequestDTO data) {
        validarRegraDeNegocio(data);
        Conta novaConta = new Conta(data);
        return new ContaResponseDTO(repository.save(novaConta));
    }

    @Transactional
    public ContaResponseDTO atualizar(Long id, ContaRequestDTO data) {
        validarRegraDeNegocio(data);

        Conta conta = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        conta.setNome(data.nome());
        conta.setTipo(data.tipo());
        conta.setRecorrencia(data.recorrencia());
        conta.setDescricao(data.descricao());
        conta.setStatus(data.status());

        // Reatribui os relacionamentos com base no novo DTO
        conta.atribuirRelacionamentos(data);

        return new ContaResponseDTO(conta);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Conta não encontrada");
        }
        repository.deleteById(id);
    }

    // A validação saiu do Controller e veio para o Service
    private void validarRegraDeNegocio(ContaRequestDTO data) {
        if (data.tipo() == TipoConta.RECEITA && data.clienteId() == null) {
            throw new IllegalArgumentException("Para contas de RECEITA, o ID do Cliente é obrigatório.");
        }
        if (data.tipo() == TipoConta.DESPESA && data.fornecedorId() == null) {
            throw new IllegalArgumentException("Para contas de DESPESA, o ID do Fornecedor é obrigatório.");
        }
    }
}