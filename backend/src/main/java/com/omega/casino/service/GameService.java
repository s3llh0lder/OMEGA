package com.omega.casino.service;

import com.omega.casino.dto.GameResponse;
import com.omega.casino.entity.Game;
import com.omega.casino.exception.GameNotFoundException;
import com.omega.casino.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public void initializeGames() {
        List<Game> games = new ArrayList<>();

        games.add(Game.builder()
                .id(1L)
                .name("Slot Machine")
                .chanceOfWinning(new BigDecimal("0.45"))
                .winningMultiplier(new BigDecimal("2.00"))
                .maxBet(new BigDecimal("100.00"))
                .minBet(new BigDecimal("1.00"))
                .build());

        games.add(Game.builder()
                .id(2L)
                .name("Roulette")
                .chanceOfWinning(new BigDecimal("0.35"))
                .winningMultiplier(new BigDecimal("3.00"))
                .maxBet(new BigDecimal("500.00"))
                .minBet(new BigDecimal("5.00"))
                .build());

        games.add(Game.builder()
                .id(3L)
                .name("Blackjack")
                .chanceOfWinning(new BigDecimal("0.49"))
                .winningMultiplier(new BigDecimal("2.00"))
                .maxBet(new BigDecimal("200.00"))
                .minBet(new BigDecimal("10.00"))
                .build());

        games.add(Game.builder()
                .id(4L)
                .name("Dice Roll")
                .chanceOfWinning(new BigDecimal("0.16"))
                .winningMultiplier(new BigDecimal("6.00"))
                .maxBet(new BigDecimal("50.00"))
                .minBet(new BigDecimal("1.00"))
                .build());

        games.add(Game.builder()
                .id(5L)
                .name("Coin Flip")
                .chanceOfWinning(new BigDecimal("0.50"))
                .winningMultiplier(new BigDecimal("2.00"))
                .maxBet(new BigDecimal("1000.00"))
                .minBet(new BigDecimal("1.00"))
                .build());

        gameRepository.saveAll(games);
    }

    public List<GameResponse> getAllGames() {
        return gameRepository.findAll().stream()
                .map(this::convertToResponse).toList();
    }

    public GameResponse getGame(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found with id: " + id));
        return convertToResponse(game);
    }

    public Game getGameEntity(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found with id: " + id));
    }

    private GameResponse convertToResponse(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .name(game.getName())
                .chanceOfWinning(game.getChanceOfWinning())
                .winningMultiplier(game.getWinningMultiplier())
                .maxBet(game.getMaxBet())
                .minBet(game.getMinBet())
                .build();
    }
}
