package com.example.loginauthapi.controller;

import com.example.loginauthapi.dto.ClienteRequestDTO;
import com.example.loginauthapi.dto.ClienteResponseDTO;
import com.example.loginauthapi.infra.security.TokenService;
import com.example.loginauthapi.model.Cliente;
import com.example.loginauthapi.model.Status;
import com.example.loginauthapi.model.TipoPessoa;
import com.example.loginauthapi.repositories.UserRepository;
import com.example.loginauthapi.services.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService service;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserRepository userRepository;

    private ClienteRequestDTO requestDTO;
    private ClienteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ClienteRequestDTO(
                "Matheus de Souza", TipoPessoa.FISICA, null, "12345678901",
                null, "RG123", "matheus@email.com", "11999999999",
                "Endereço Teste", "Desc", Status.ATIVO
        );

        Cliente clienteSimulado = new Cliente(requestDTO);
        clienteSimulado.setId(1L);
        responseDTO = new ClienteResponseDTO(clienteSimulado);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes - Deve listar todos os clientes")
    void deveListarClientes() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nomeOuNomeFantasia").value("Matheus de Souza"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{id} - Deve buscar cliente por ID")
    void deveBuscarClientePorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/clientes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeOuNomeFantasia").value("Matheus de Souza"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /clientes - Deve cadastrar novo cliente")
    void deveCadastrarCliente() throws Exception {
        when(service.salvar(any(ClienteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/clientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /clientes/{id} - Deve atualizar cliente existente")
    void deveAtualizarCliente() throws Exception {
        when(service.atualizar(eq(1L), any(ClienteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/clientes/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeOuNomeFantasia").value("Matheus de Souza"));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /clientes/{id} - Deve excluir cliente")
    void deveExcluirCliente() throws Exception {
        doNothing().when(service).excluir(1L);

        mockMvc.perform(delete("/clientes/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}