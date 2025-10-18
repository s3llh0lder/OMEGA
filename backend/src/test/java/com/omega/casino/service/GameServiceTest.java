package com.omega.casino.service;

import com.omega.casino.dto.GameResponse;
import com.omega.casino.entity.Game;
import com.omega.casino.exception.GameNotFoundException;
import com.omega.casino.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void testInitializeGames() {
        gameService.initializeGames();

        verify(gameRepository).saveAll(anyList());
    }

    @Test
    void testGetAllGames_Success() {
        Game game1 = Game.builder()
                .id(1L)
                .name("Slot Machine")
                .chanceOfWinning(BigDecimal.valueOf(0.45))
                .winningMultiplier(BigDecimal.valueOf(2.00))
                .maxBet(BigDecimal.valueOf(100))
                .minBet(BigDecimal.valueOf(1))
                .build();

        Game game2 = Game.builder()
                .id(2L)
                .name("Roulette")
                .chanceOfWinning(BigDecimal.valueOf(0.35))
                .winningMultiplier(BigDecimal.valueOf(3.00))
                .maxBet(BigDecimal.valueOf(500))
                .minBet(BigDecimal.valueOf(5))
                .build();

        when(gameRepository.findAll()).thenReturn(Arrays.asList(game1, game2));

        List<GameResponse> games = gameService.getAllGames();

        assertNotNull(games);
        assertEquals(2, games.size());
        assertEquals("Slot Machine", games.get(0).getName());
        assertEquals("Roulette", games.get(1).getName());
    }

    @Test
    void testGetAllGames_Empty() {
        when(gameRepository.findAll()).thenReturn(Arrays.asList());

        List<GameResponse> games = gameService.getAllGames();

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    void testGetGame_Success() {
        Game game = Game.builder()
                .id(1L)
                .name("Slot Machine")
                .chanceOfWinning(BigDecimal.valueOf(0.45))
                .winningMultiplier(BigDecimal.valueOf(2.00))
                .maxBet(BigDecimal.valueOf(100))
                .minBet(BigDecimal.valueOf(1))
                .build();

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        GameResponse response = gameService.getGame(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Slot Machine", response.getName());
        assertEquals(BigDecimal.valueOf(0.45), response.getChanceOfWinning());
    }

    @Test
    void testGetGame_NotFound() {
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.getGame(999L));
    }

    @Test
    void testGetGameEntity_Success() {
        Game game = Game.builder()
                .id(1L)
                .name("Slot Machine")
                .chanceOfWinning(BigDecimal.valueOf(0.45))
                .winningMultiplier(BigDecimal.valueOf(2.00))
                .maxBet(BigDecimal.valueOf(100))
                .minBet(BigDecimal.valueOf(1))
                .build();

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        Game result = gameService.getGameEntity(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Slot Machine", result.getName());
    }

    @Test
    void testGetGameEntity_NotFound() {
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.getGameEntity(999L));
    }
}
