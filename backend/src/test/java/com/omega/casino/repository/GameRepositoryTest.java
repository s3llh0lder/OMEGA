package com.omega.casino.repository;

import com.omega.casino.entity.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GameRepositoryTest {

    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository = new GameRepository();
    }

    @Test
    void testSaveAll_SavesAllGames() {
        Game game1 = Game.builder()
                .id(1L)
                .name("Game 1")
                .chanceOfWinning(BigDecimal.valueOf(0.5))
                .winningMultiplier(BigDecimal.valueOf(2.0))
                .maxBet(BigDecimal.valueOf(100))
                .minBet(BigDecimal.valueOf(1))
                .build();

        Game game2 = Game.builder()
                .id(2L)
                .name("Game 2")
                .chanceOfWinning(BigDecimal.valueOf(0.3))
                .winningMultiplier(BigDecimal.valueOf(3.0))
                .maxBet(BigDecimal.valueOf(200))
                .minBet(BigDecimal.valueOf(5))
                .build();

        gameRepository.saveAll(Arrays.asList(game1, game2));

        List<Game> allGames = gameRepository.findAll();
        assertEquals(2, allGames.size());
    }

    @Test
    void testFindById_GameExists() {
        Game game = Game.builder()
                .id(1L)
                .name("Test Game")
                .chanceOfWinning(BigDecimal.valueOf(0.5))
                .winningMultiplier(BigDecimal.valueOf(2.0))
                .maxBet(BigDecimal.valueOf(100))
                .minBet(BigDecimal.valueOf(1))
                .build();

        gameRepository.saveAll(Arrays.asList(game));
        Optional<Game> found = gameRepository.findById(1L);

        assertTrue(found.isPresent());
        assertEquals("Test Game", found.get().getName());
    }

    @Test
    void testFindById_GameNotExists() {
        Optional<Game> found = gameRepository.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll_EmptyRepository() {
        List<Game> games = gameRepository.findAll();
        assertTrue(games.isEmpty());
    }

    @Test
    void testFindAll_ReturnsAllGames() {
        Game game1 = Game.builder()
                .id(1L)
                .name("Game 1")
                .chanceOfWinning(BigDecimal.valueOf(0.5))
                .winningMultiplier(BigDecimal.valueOf(2.0))
                .maxBet(BigDecimal.valueOf(100))
                .minBet(BigDecimal.valueOf(1))
                .build();

        Game game2 = Game.builder()
                .id(2L)
                .name("Game 2")
                .chanceOfWinning(BigDecimal.valueOf(0.3))
                .winningMultiplier(BigDecimal.valueOf(3.0))
                .maxBet(BigDecimal.valueOf(200))
                .minBet(BigDecimal.valueOf(5))
                .build();

        Game game3 = Game.builder()
                .id(3L)
                .name("Game 3")
                .chanceOfWinning(BigDecimal.valueOf(0.4))
                .winningMultiplier(BigDecimal.valueOf(2.5))
                .maxBet(BigDecimal.valueOf(150))
                .minBet(BigDecimal.valueOf(2))
                .build();

        gameRepository.saveAll(Arrays.asList(game1, game2, game3));
        List<Game> allGames = gameRepository.findAll();

        assertEquals(3, allGames.size());
    }
}
