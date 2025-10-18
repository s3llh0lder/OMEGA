package com.omega.casino.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omega.casino.dto.DepositRequest;
import com.omega.casino.dto.PlayerRegistrationRequest;
import com.omega.casino.dto.PlayerResponse;
import com.omega.casino.exception.PlayerNotFoundException;
import com.omega.casino.exception.UnderagePlayerException;
import com.omega.casino.exception.UsernameAlreadyExistsException;
import com.omega.casino.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerController.class)
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlayerService playerService;

    @Test
    void testRegisterPlayer_Success() throws Exception {
        PlayerRegistrationRequest request = PlayerRegistrationRequest.builder()
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        PlayerResponse response = PlayerResponse.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.ZERO)
                .build();

        when(playerService.registerPlayer(any(PlayerRegistrationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/players/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void testRegisterPlayer_UnderAge() throws Exception {
        PlayerRegistrationRequest request = PlayerRegistrationRequest.builder()
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.now().minusYears(17))
                .build();

        when(playerService.registerPlayer(any(PlayerRegistrationRequest.class)))
                .thenThrow(new UnderagePlayerException("Player must be at least 18 years old"));

        mockMvc.perform(post("/api/players/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterPlayer_UsernameExists() throws Exception {
        PlayerRegistrationRequest request = PlayerRegistrationRequest.builder()
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        when(playerService.registerPlayer(any(PlayerRegistrationRequest.class)))
                .thenThrow(new UsernameAlreadyExistsException("Username already exists"));

        mockMvc.perform(post("/api/players/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterPlayer_InvalidData() throws Exception {
        PlayerRegistrationRequest request = PlayerRegistrationRequest.builder()
                .name("")
                .username("")
                .birthdate(null)
                .build();

        mockMvc.perform(post("/api/players/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPlayer_Success() throws Exception {
        PlayerResponse response = PlayerResponse.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.valueOf(100))
                .build();

        when(playerService.getPlayer(1L)).thenReturn(response);

        mockMvc.perform(get("/api/players/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.balance").value(100));
    }

    @Test
    void testGetPlayer_NotFound() throws Exception {
        when(playerService.getPlayer(999L)).thenThrow(new PlayerNotFoundException("Player not found"));

        mockMvc.perform(get("/api/players/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeposit_Success() throws Exception {
        DepositRequest request = DepositRequest.builder()
                .playerId(1L)
                .amount(BigDecimal.valueOf(50))
                .build();

        PlayerResponse response = PlayerResponse.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.valueOf(150))
                .build();

        when(playerService.deposit(any(DepositRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/players/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150));
    }

    @Test
    void testDeposit_PlayerNotFound() throws Exception {
        DepositRequest request = DepositRequest.builder()
                .playerId(999L)
                .amount(BigDecimal.valueOf(50))
                .build();

        when(playerService.deposit(any(DepositRequest.class)))
                .thenThrow(new PlayerNotFoundException("Player not found"));

        mockMvc.perform(post("/api/players/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
