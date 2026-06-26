package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.ClienteRequestDTO;
import com.example.loginauthapi.dto.ClienteResponseDTO;
import com.example.loginauthapi.model.Cliente;
import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoPessoa;
import com.example.loginauthapi.repositories.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    private Cliente clienteMock;
    private ClienteRequestDTO requestDTOMock;

    @BeforeEach
    void setUp() {
        requestDTOMock = new ClienteRequestDTO(
                "João da Silva", TipoPessoa.FISICA, null, "11122233344",
                null, "MG-12345", "joao@email.com", "31999999999",
                "Rua A", "Cliente fiel", Status.ATIVO
        );

        clienteMock = new Cliente(requestDTOMock);
        clienteMock.setId(1L);
    }

    @Test
    @DisplayName("Deve salvar e retornar o DTO do cliente")
    void deveSalvarClienteComSucesso() {
        when(repository.save(any(Cliente.class))).thenReturn(clienteMock);

        ClienteResponseDTO response = service.salvar(requestDTOMock);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("João da Silva", response.nomeOuNomeFantasia());
        verify(repository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve listar todos os clientes convertendo para DTO")
    void deveListarTodosClientes() {
        when(repository.findAll()).thenReturn(List.of(clienteMock));

        List<ClienteResponseDTO> lista = service.listarTodos();

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertEquals("11122233344", lista.get(0).cpfCnpj());
    }

    @Test
    @DisplayName("Deve buscar cliente por ID existente")
    void deveBuscarClientePorIdComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(clienteMock));

        ClienteResponseDTO response = service.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao buscar ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve excluir cliente quando ID existir")
    void deveExcluirClienteComSucesso() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.excluir(1L));
        verify(repository, times(1)).deleteById(1L);
    }

    @Test // <--- ESSA LINHA É A CHAVE PARA SAIR DO 0%
    @DisplayName("Deve atualizar cliente existente com sucesso")
    void deveAtualizarCliente() {
        // Mockando o comportamento de encontrar o cliente
        when(repository.findById(1L)).thenReturn(Optional.of(clienteMock));

        ClienteResponseDTO resultado = service.atualizar(1L, requestDTOMock);

        assertNotNull(resultado);
        assertEquals(requestDTOMock.nomeOuNomeFantasia(), resultado.nomeOuNomeFantasia());
        verify(repository, times(1)).findById(1L);
    }

    @Test // Cobre a lambda$atualizar$1 (o erro no findById)
    @DisplayName("Deve lançar exceção ao atualizar ID inexistente")
    void deveLancarExcecaoAoAtualizarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.atualizar(99L, requestDTOMock));
    }

    @Test // Cobre a Branch faltante do excluir (o "if" do existsById)
    @DisplayName("Deve lançar exceção ao tentar excluir ID inexistente")
    void deveLancarExcecaoAoExcluirIdInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.excluir(99L));
        verify(repository, never()).deleteById(anyLong());
    }

}