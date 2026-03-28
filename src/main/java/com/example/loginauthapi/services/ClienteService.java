package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.ClienteRequestDTO;
import com.example.loginauthapi.dto.ClienteResponseDTO;
import com.example.loginauthapi.model.Cliente;
import com.example.loginauthapi.repositories.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor // Substitui o @Autowired, melhor para testes
public class ClienteService {

    private final ClienteRepository repository;

    public List<ClienteResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(ClienteResponseDTO::new)
                .toList();
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));
        return new ClienteResponseDTO(cliente);
    }

    @Transactional
    public ClienteResponseDTO salvar(ClienteRequestDTO data) {
        Cliente novoCliente = new Cliente(data);
        novoCliente.validarConsistenciaDados();
        return new ClienteResponseDTO(repository.save(novoCliente));
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO data) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        // Atualização dos campos herdados e específicos
        cliente.setNomeOuNomeFantasia(data.nomeOuNomeFantasia());
        cliente.setTipoPessoa(data.tipoPessoa());
        cliente.setCpfCnpj(data.cpfCnpj());
        cliente.setRg(data.rg());
        cliente.setEmail(data.email());
        cliente.setTelefone(data.telefone());
        cliente.setEndereco(data.endereco());
        cliente.setDescricao(data.descricao());
        cliente.setStatus(data.status());

        return new ClienteResponseDTO(cliente);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado");
        }
        repository.deleteById(id);
    }
}