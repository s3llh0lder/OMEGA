package com.omega.casino.controller;

import com.omega.casino.dto.GameResponse;
import com.omega.casino.exception.GameNotFoundException;
import com.omega.casino.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    void testGetAllGames_Success() throws Exception {
        GameResponse game1 = GameResponse.builder()
                .id(1L)
                .name("Slot Machine")
                .chanceOfWinning(BigDecimal.valueOf(0.45))
                .winningMultiplier(BigDecimal.valueOf(2.00))
                .maxBet(BigDecimal.valueOf(100))
                .minBet(BigDecimal.valueOf(1))
                .build();

        GameResponse game2 = GameResponse.builder()
                .id(2L)
                .name("Roulette")
                .chanceOfWinning(BigDecimal.valueOf(0.35))
                .winningMultiplier(BigDecimal.valueOf(3.00))
                .maxBet(BigDecimal.valueOf(500))
                .minBet(BigDecimal.valueOf(5))
                .build();

        List<GameResponse> games = Arrays.asList(game1, game2);
        when(gameService.getAllGames()).thenReturn(games);

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Slot Machine"))
                .andExpect(jsonPath("$[1].name").value("Roulette"));
    }

    @Test
    void testGetAllGames_Empty() throws Exception {
        when(gameService.getAllGames()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetGame_Success() throws Exception {
        GameResponse game = GameResponse.builder()
                .id(1L)
                .name("Slot Machine")
                .chanceOfWinning(BigDecimal.valueOf(0.45))
                .winningMultiplier(BigDecimal.valueOf(2.00))
                .maxBet(BigDecimal.valueOf(100))
                .minBet(BigDecimal.valueOf(1))
                .build();

        when(gameService.getGame(1L)).thenReturn(game);

        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Slot Machine"))
                .andExpect(jsonPath("$.chanceOfWinning").value(0.45))
                .andExpect(jsonPath("$.maxBet").value(100));
    }

    @Test
    void testGetGame_NotFound() throws Exception {
        when(gameService.getGame(999L)).thenThrow(new GameNotFoundException("Game not found"));

        mockMvc.perform(get("/api/games/999"))
                .andExpect(status().isNotFound());
    }
}
