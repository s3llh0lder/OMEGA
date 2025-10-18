package com.omega.casino.service;

import com.omega.casino.dto.BetResponse;
import com.omega.casino.dto.PlaceBetRequest;
import com.omega.casino.entity.Bet;
import com.omega.casino.entity.BetResult;
import com.omega.casino.entity.Game;
import com.omega.casino.entity.Player;
import com.omega.casino.exception.InvalidBetException;
import com.omega.casino.repository.BetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetServiceTest {

    @Mock
    private BetRepository betRepository;

    @Mock
    private PlayerService playerService;

    @Mock
    private GameService gameService;

    @InjectMocks
    private BetService betService;

    @Test
    void testPlaceBet_BetBelowMinimum() {
        PlaceBetRequest request = PlaceBetRequest.builder()
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(0.50))
                .build();

        Player player = Player.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.valueOf(100))
                .build();

        Game game = Game.builder()
                .id(1L)
                .name("Slot Machine")
                .chanceOfWinning(BigDecimal.valueOf(0.45))
                .winningMultiplier(BigDecimal.valueOf(2.00))
                .maxBet(BigDecimal.valueOf(100))
                .minBet(BigDecimal.valueOf(1.00))
                .build();

        when(playerService.getPlayerEntity(1L)).thenReturn(player);
        when(gameService.getGameEntity(1L)).thenReturn(game);

        assertThrows(InvalidBetException.class, () -> betService.placeBet(request));
        verify(playerService, never()).deductBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void testPlaceBet_BetAboveMaximum() {
        PlaceBetRequest request = PlaceBetRequest.builder()
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(150))
                .build();

        Player player = Player.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.valueOf(200))
                .build();

        Game game = Game.builder()
                .id(1L)
                .name("Slot Machine")
                .chanceOfWinning(BigDecimal.valueOf(0.45))
                .winningMultiplier(BigDecimal.valueOf(2.00))
                .maxBet(BigDecimal.valueOf(100))
                .minBet(BigDecimal.valueOf(1.00))
                .build();

        when(playerService.getPlayerEntity(1L)).thenReturn(player);
        when(gameService.getGameEntity(1L)).thenReturn(game);

        assertThrows(InvalidBetException.class, () -> betService.placeBet(request));
        verify(playerService, never()).deductBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void testPlaceBet_ValidBet() {
        PlaceBetRequest request = PlaceBetRequest.builder()
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(10))
                .build();

        Player player = Player.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.valueOf(100))
                .build();

        Player updatedPlayer = Player.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.valueOf(90))
                .build();

        Game game = Game.builder()
                .id(1L)
                .name("Slot Machine")
                .chanceOfWinning(BigDecimal.valueOf(0.45))
                .winningMultiplier(BigDecimal.valueOf(2.00))
                .maxBet(BigDecimal.valueOf(100))
                .minBet(BigDecimal.valueOf(1.00))
                .build();

        Bet savedBet = Bet.builder()
                .id(1L)
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(10))
                .result(BetResult.LOSE)
                .payout(BigDecimal.ZERO)
                .build();

        when(playerService.getPlayerEntity(1L)).thenReturn(player, updatedPlayer);
        when(gameService.getGameEntity(1L)).thenReturn(game);
        when(betRepository.save(any(Bet.class))).thenReturn(savedBet);
        doNothing().when(playerService).deductBalance(anyLong(), any(BigDecimal.class));

        BetResponse response = betService.placeBet(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Slot Machine", response.getGameName());
        assertNotNull(response.getResult());
        verify(playerService).deductBalance(eq(1L), any(BigDecimal.class));
        verify(betRepository).save(any(Bet.class));
    }

    @Test
    void testPlaceBet_DeductsBalance() {
        PlaceBetRequest request = PlaceBetRequest.builder()
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(10))
                .build();

        Player player = Player.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.valueOf(100))
                .build();

        Game game = Game.builder()
                .id(1L)
                .name("Slot Machine")
                .chanceOfWinning(BigDecimal.valueOf(0.45))
                .winningMultiplier(BigDecimal.valueOf(2.00))
                .maxBet(BigDecimal.valueOf(100))
                .minBet(BigDecimal.valueOf(1.00))
                .build();

        Bet savedBet = Bet.builder()
                .id(1L)
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(10))
                .result(BetResult.WIN)
                .payout(BigDecimal.valueOf(20))
                .build();

        when(playerService.getPlayerEntity(1L)).thenReturn(player);
        when(gameService.getGameEntity(1L)).thenReturn(game);
        when(betRepository.save(any(Bet.class))).thenReturn(savedBet);

        betService.placeBet(request);

        verify(playerService).deductBalance(eq(1L), any(BigDecimal.class));
    }
}
