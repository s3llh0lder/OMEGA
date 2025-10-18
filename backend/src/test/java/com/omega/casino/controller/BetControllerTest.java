package com.omega.casino.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omega.casino.dto.BetResponse;
import com.omega.casino.dto.PlaceBetRequest;
import com.omega.casino.entity.BetResult;
import com.omega.casino.exception.GameNotFoundException;
import com.omega.casino.exception.InsufficientBalanceException;
import com.omega.casino.exception.InvalidBetException;
import com.omega.casino.exception.PlayerNotFoundException;
import com.omega.casino.service.BetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BetController.class)
class BetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BetService betService;

    @Test
    void testPlaceBet_Success() throws Exception {
        PlaceBetRequest request = PlaceBetRequest.builder()
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(10))
                .build();

        BetResponse response = BetResponse.builder()
                .id(1L)
                .playerId(1L)
                .gameId(1L)
                .gameName("Slot Machine")
                .betValue(BigDecimal.valueOf(10))
                .placedAt(LocalDateTime.now())
                .result(BetResult.WIN)
                .payout(BigDecimal.valueOf(20))
                .newBalance(BigDecimal.valueOf(110))
                .build();

        when(betService.placeBet(any(PlaceBetRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/bets/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.gameName").value("Slot Machine"))
                .andExpect(jsonPath("$.result").value("WIN"))
                .andExpect(jsonPath("$.payout").value(20))
                .andExpect(jsonPath("$.newBalance").value(110));
    }

    @Test
    void testPlaceBet_PlayerNotFound() throws Exception {
        PlaceBetRequest request = PlaceBetRequest.builder()
                .playerId(999L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(10))
                .build();

        when(betService.placeBet(any(PlaceBetRequest.class)))
                .thenThrow(new PlayerNotFoundException("Player not found"));

        mockMvc.perform(post("/api/bets/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPlaceBet_GameNotFound() throws Exception {
        PlaceBetRequest request = PlaceBetRequest.builder()
                .playerId(1L)
                .gameId(999L)
                .betValue(BigDecimal.valueOf(10))
                .build();

        when(betService.placeBet(any(PlaceBetRequest.class)))
                .thenThrow(new GameNotFoundException("Game not found"));

        mockMvc.perform(post("/api/bets/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPlaceBet_InsufficientBalance() throws Exception {
        PlaceBetRequest request = PlaceBetRequest.builder()
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(100))
                .build();

        when(betService.placeBet(any(PlaceBetRequest.class)))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));

        mockMvc.perform(post("/api/bets/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPlaceBet_InvalidBetValue() throws Exception {
        PlaceBetRequest request = PlaceBetRequest.builder()
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(0.50))
                .build();

        when(betService.placeBet(any(PlaceBetRequest.class)))
                .thenThrow(new InvalidBetException("Bet value must be at least 1.00"));

        mockMvc.perform(post("/api/bets/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPlaceBet_ValidationError() throws Exception {
        PlaceBetRequest request = PlaceBetRequest.builder()
                .playerId(null)
                .gameId(null)
                .betValue(null)
                .build();

        mockMvc.perform(post("/api/bets/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
