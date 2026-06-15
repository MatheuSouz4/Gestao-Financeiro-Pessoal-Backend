package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.ContaRequestDTO;
import com.example.loginauthapi.dto.ContaResponseDTO;
import com.example.loginauthapi.model.Conta;
import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoConta;
import com.example.loginauthapi.repositories.ContaRepository;
import com.example.loginauthapi.repositories.ClienteRepository;
import com.example.loginauthapi.repositories.FornecedorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository repository;
    private final ClienteRepository clienteRepository;
    private final FornecedorRepository fornecedorRepository;

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
        validarDuplicidadeConta(data, null); // Passamos null porque é uma conta nova

        var novaConta = new Conta(data);
        return new ContaResponseDTO(repository.save(novaConta));
    }

    @Transactional
    public ContaResponseDTO atualizar(Long id, ContaRequestDTO data) {
        validarVinculoObrigatorio(data);
        validarDuplicidadeConta(data, id); // Passamos o ID para evitar falso positivo com ela mesma

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

     //Valida os vínculos e impede relacionamentos com entidades INATIVAS
    private void validarVinculoObrigatorio(ContaRequestDTO data) {
        if (data.tipo() == TipoConta.RECEITA) {
            if (data.clienteId() == null) {
                throw new IllegalArgumentException("Receitas exigem um Cliente vinculado.");
            }
            var cliente = clienteRepository.findById(data.clienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente referenciado não existe."));

            if (cliente.getStatus() == Status.INATIVO || cliente.getStatus() == Status.BLOQUEADO) {
                throw new IllegalArgumentException("Não é possível vincular uma Receita a um Cliente INATIVO ou BLOQUEADO.");
            }
        }

        if (data.tipo() == TipoConta.DESPESA) {
            if (data.fornecedorId() == null) {
                throw new IllegalArgumentException("Despesas exigem um Fornecedor vinculado.");
            }
            var fornecedor = fornecedorRepository.findById(data.fornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor referenciado não existe."));

            if (fornecedor.getStatus() == Status.INATIVO || fornecedor.getStatus() == Status.BLOQUEADO) {
                throw new IllegalArgumentException("Não é possível vincular uma Despesa a um Fornecedor INATIVO ou BLOQUEADO.");
            }
        }
    }

      //Valida se já existe uma conta com o mesmo nome, mesmo tipo e para o mesmo cliente/fornecedor.
    private void validarDuplicidadeConta(ContaRequestDTO data, Long idAtual) {
        String nomeFormatado = data.nome().trim(); // Remove espaços extras no início e fim

        if (data.tipo() == TipoConta.RECEITA) {
            Optional<Conta> existente = repository.findByNomeIgnoreCaseAndTipoAndClienteId(
                    nomeFormatado, data.tipo(), data.clienteId()
            );

            // Se encontrou uma conta e o ID dela for diferente do ID que estamos atualizando (ou se for um cadastro novo)
            if (existente.isPresent() && !existente.get().getId().equals(idAtual)) {
                throw new IllegalArgumentException("Já existe uma Receita cadastrada com o nome '" + nomeFormatado + "' para este Cliente.");
            }
        } else if (data.tipo() == TipoConta.DESPESA) {
            Optional<Conta> existente = repository.findByNomeIgnoreCaseAndTipoAndFornecedorId(
                    nomeFormatado, data.tipo(), data.fornecedorId()
            );

            if (existente.isPresent() && !existente.get().getId().equals(idAtual)) {
                throw new IllegalArgumentException("Já existe uma Despesa cadastrada com o nome '" + nomeFormatado + "' para este Fornecedor.");
            }
        }
    }

    public List<ContaResponseDTO> listarComFiltros(Status status, TipoConta tipo) {
        return repository.findByFiltros(status, tipo).stream()
                .map(ContaResponseDTO::new)
                .toList();
    }

    public BigDecimal obterSaldoAtual(Long id) {
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        return conta.getSaldoAtual();
    }
}