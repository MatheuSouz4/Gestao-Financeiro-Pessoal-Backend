package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.FornecedorRequestDTO;
import com.example.loginauthapi.dto.FornecedorResponseDTO;
import com.example.loginauthapi.model.Fornecedor;
import com.example.loginauthapi.repositories.FornecedorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor // Injeção de dependência via construtor (mais seguro que @Autowired)
public class FornecedorService {

    private final FornecedorRepository repository;

    public List<FornecedorResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(FornecedorResponseDTO::new)
                .toList();
    }

    public FornecedorResponseDTO buscarPorId(Long id) {
        Fornecedor fornecedor = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor não encontrado com ID: " + id));
        return new FornecedorResponseDTO(fornecedor);
    }

    @Transactional
    public FornecedorResponseDTO salvar(FornecedorRequestDTO data) {
        Fornecedor novoFornecedor = new Fornecedor(data);
        novoFornecedor.validarConsistenciaDados();
        return new FornecedorResponseDTO(repository.save(novoFornecedor));
    }

    @Transactional
    public FornecedorResponseDTO atualizar(Long id, FornecedorRequestDTO data) {
        Fornecedor fornecedor = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor não encontrado"));

        // Atualização dos campos herdados e específicos
        fornecedor.setNomeOuNomeFantasia(data.nomeOuNomeFantasia());
        fornecedor.setRazaoSocial(data.razaoSocial());
        fornecedor.setTipoPessoa(data.tipoPessoa());
        fornecedor.setCpfCnpj(data.cpfCnpj());
        fornecedor.setInscricaoEstadual(data.inscricaoEstadual());
        fornecedor.setEmail(data.email());
        fornecedor.setTelefone(data.telefone());
        fornecedor.setEndereco(data.endereco());
        fornecedor.setDescricao(data.descricao());
        fornecedor.setStatus(data.status());

        return new FornecedorResponseDTO(fornecedor);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Fornecedor não encontrado");
        }
        repository.deleteById(id);
    }
}
