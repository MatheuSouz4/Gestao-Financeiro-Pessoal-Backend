package com.example.loginauthapi.service;

import com.example.loginauthapi.domain.cadastro.Conta;
import com.example.loginauthapi.repositories.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Marca a classe como um componente de serviço Spring
public class ContaService {

    @Autowired // Injeção de dependência do repositório
    private ContaRepository contaRepository;

    /**
     * Retorna todas as contas cadastradas.
     * @return Lista de contas.
     */
    public List<Conta> findAll() {
        // O findAll() padrão do JpaRepository já retorna a lista.
        return contaRepository.findAll();
    }

    public Optional<Conta> findById(String id) {
        return contaRepository.findById(id);
    }

    public Conta save(Conta conta) {
        // Persiste a conta no banco de dados.
        return contaRepository.save(conta);
    }


    public Optional<Conta> update(String id, Conta contaDetalhes) {
        return contaRepository.findById(id).map(contaExistente -> {
            // Mapeia os campos atualizáveis da contaDetalhes para a contaExistente
            contaExistente.setNome(contaDetalhes.getNome());
            contaExistente.setDescricao(contaDetalhes.getDescricao());
            contaExistente.setTipo(contaDetalhes.getTipo());
            contaExistente.setRecorrencia(contaDetalhes.getRecorrencia());

            // Lógica para Cliente/Fornecedor (garante que apenas um esteja ativo)
            if (contaDetalhes.getTipo() == com.example.loginauthapi.domain.cadastro.TipoConta.RECEITA) {
                contaExistente.setClienteId(contaDetalhes.getClienteId());
                contaExistente.setFornecedorId(null); // Limpa o fornecedor
            } else {
                contaExistente.setFornecedorId(contaDetalhes.getFornecedorId());
                contaExistente.setClienteId(null); // Limpa o cliente
            }

            return contaRepository.save(contaExistente);
        });
    }

    /**
     * Deleta uma conta pelo ID.
     * @param id O ID da conta a ser deletada.
     * @return true se a conta foi encontrada e deletada, false caso contrário.
     */
    public boolean delete(String id) {
        if (contaRepository.existsById(id)) {
            contaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}