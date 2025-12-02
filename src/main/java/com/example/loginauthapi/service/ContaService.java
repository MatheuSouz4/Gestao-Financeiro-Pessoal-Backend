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

    /**
     * Busca uma conta pelo ID.
     * @param id O ID da conta.
     * @return Um Optional contendo a Conta, se encontrada.
     */
    public Optional<Conta> findById(Long id) {
        return contaRepository.findById(id);
    }

    /**
     * Salva uma nova conta ou atualiza uma existente.
     * @param conta O objeto Conta a ser salvo.
     * @return A conta salva/atualizada.
     */
    public Conta save(Conta conta) {
        // Persiste a conta no banco de dados.
        return contaRepository.save(conta);
    }

    /**
     * Atualiza uma conta existente.
     * @param id O ID da conta a ser atualizada.
     * @param contaDetalhes O objeto Conta com os novos detalhes.
     * @return A conta atualizada, ou um Optional vazio se não for encontrada.
     */
    public Optional<Conta> update(Long id, Conta contaDetalhes) {
        return contaRepository.findById(id).map(contaExistente -> {
            // Mapeia os campos atualizáveis da contaDetalhes para a contaExistente
            contaExistente.setNome(contaDetalhes.getNome());
            contaExistente.setDescricao(contaDetalhes.getDescricao());
            contaExistente.setTipo(contaDetalhes.getTipo());
            contaExistente.setRecorrencia(contaDetalhes.getRecorrencia());

            // Lógica para Cliente/Fornecedor (garante que apenas um esteja ativo)
            if (contaDetalhes.getTipo() == com.example.loginauthapi.domain.enums.TipoConta.RECEITA) {
                contaExistente.setCliente(contaDetalhes.getCliente());
                contaExistente.setFornecedor(null); // Limpa o fornecedor
            } else {
                contaExistente.setFornecedor(contaDetalhes.getFornecedor());
                contaExistente.setCliente(null); // Limpa o cliente
            }

            return contaRepository.save(contaExistente);
        });
    }

    /**
     * Deleta uma conta pelo ID.
     * @param id O ID da conta a ser deletada.
     * @return true se a conta foi encontrada e deletada, false caso contrário.
     */
    public boolean delete(Long id) {
        if (contaRepository.existsById(id)) {
            contaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}