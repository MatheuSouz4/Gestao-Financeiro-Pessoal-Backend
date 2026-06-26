package com.example.loginauthapi.services;

import com.example.loginauthapi.dto.FornecedorRequestDTO;
import com.example.loginauthapi.dto.FornecedorResponseDTO;
import com.example.loginauthapi.model.Fornecedor;
import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoPessoa;
import com.example.loginauthapi.repositories.FornecedorRepository;
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
class FornecedorServiceTest {

    @Mock
    private FornecedorRepository repository;

    @InjectMocks
    private FornecedorService service;

    private Fornecedor fornecedor;
    private FornecedorRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new FornecedorRequestDTO(
                "Distribuidora SA", "Razão SA", TipoPessoa.JURIDICA,
                "12345678000199", "IS123", null, "contato@dist.com",
                "11999999999", "Rua X", "Desc", Status.ATIVO
        );
        fornecedor = new Fornecedor(requestDTO);
        fornecedor.setId(1L);
    }

    @Test
    @DisplayName("Deve listar todos os fornecedores")
    void deveListarTodos() {
        when(repository.findAll()).thenReturn(List.of(fornecedor));
        List<FornecedorResponseDTO> resultado = service.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Distribuidora SA", resultado.get(0).nomeOuNomeFantasia());
    }

    @Test
    @DisplayName("Deve buscar fornecedor por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(fornecedor));
        FornecedorResponseDTO resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve salvar novo fornecedor")
    void deveSalvarFornecedor() {
        when(repository.save(any(Fornecedor.class))).thenReturn(fornecedor);
        FornecedorResponseDTO resultado = service.salvar(requestDTO);

        assertNotNull(resultado);
        assertEquals("Distribuidora SA", resultado.nomeOuNomeFantasia());
        verify(repository, times(1)).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("Deve atualizar fornecedor existente")
    void deveAtualizarFornecedor() {
        when(repository.findById(1L)).thenReturn(Optional.of(fornecedor));

        FornecedorResponseDTO resultado = service.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals("Distribuidora SA", resultado.nomeOuNomeFantasia());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar ID inexistente")
    void deveLancarExcecaoAoAtualizarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.atualizar(99L, requestDTO));
    }

    @Test
    @DisplayName("Deve excluir fornecedor existente")
    void deveExcluirFornecedor() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.excluir(1L));
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir ID inexistente")
    void deveLancarExcecaoAoExcluirIdInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.excluir(99L));
        verify(repository, never()).deleteById(anyLong());
    }
}