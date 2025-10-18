package com.omega.casino.service;

import com.omega.casino.dto.BetResponse;
import com.omega.casino.dto.PlaceBetRequest;
import com.omega.casino.entity.Bet;
import com.omega.casino.entity.BetResult;
import com.omega.casino.entity.Game;
import com.omega.casino.entity.Player;
import com.omega.casino.exception.InvalidBetException;
import com.omega.casino.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BetService {

    private final BetRepository betRepository;
    private final PlayerService playerService;
    private final GameService gameService;
    private final Random random = new Random();

    public BetResponse placeBet(PlaceBetRequest request) {

        Player player = playerService.getPlayerEntity(request.getPlayerId());

        Game game = gameService.getGameEntity(request.getGameId());

        BigDecimal betValue = request.getBetValue().setScale(2, RoundingMode.HALF_UP);
        if (betValue.compareTo(game.getMinBet()) < 0) {
            throw new InvalidBetException("Bet value must be at least " + game.getMinBet());
        }
        if (betValue.compareTo(game.getMaxBet()) > 0) {
            throw new InvalidBetException("Bet value cannot exceed " + game.getMaxBet());
        }

        playerService.deductBalance(player.getId(), betValue);

        boolean isWin = isWin(game.getChanceOfWinning());
        BetResult result = isWin ? BetResult.WIN : BetResult.LOSE;

        BigDecimal payout = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (isWin) {
            payout = betValue.multiply(game.getWinningMultiplier()).setScale(2, RoundingMode.HALF_UP);
            playerService.addBalance(player.getId(), payout);
        }

        Bet bet = Bet.builder()
                .playerId(player.getId())
                .gameId(game.getId())
                .betValue(betValue)
                .placedAt(LocalDateTime.now())
                .result(result)
                .payout(payout)
                .build();

        Bet savedBet = betRepository.save(bet);

        Player updatedPlayer = playerService.getPlayerEntity(player.getId());

        return BetResponse.builder()
                .id(savedBet.getId())
                .playerId(savedBet.getPlayerId())
                .gameId(savedBet.getGameId())
                .gameName(game.getName())
                .betValue(savedBet.getBetValue())
                .placedAt(savedBet.getPlacedAt())
                .result(savedBet.getResult())
                .payout(savedBet.getPayout())
                .newBalance(updatedPlayer.getBalance())
                .build();
    }

    private boolean isWin(BigDecimal chanceOfWinning) {
        double randomValue = random.nextDouble();
        return randomValue < chanceOfWinning.doubleValue();
    }
}
