package com.omega.casino.service;

import com.omega.casino.dto.DepositRequest;
import com.omega.casino.dto.PlayerRegistrationRequest;
import com.omega.casino.dto.PlayerResponse;
import com.omega.casino.entity.Player;
import com.omega.casino.exception.InsufficientBalanceException;
import com.omega.casino.exception.PlayerNotFoundException;
import com.omega.casino.exception.UnderagePlayerException;
import com.omega.casino.exception.UsernameAlreadyExistsException;
import com.omega.casino.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class PlayerService {

    public static final String PLAYER_NOT_FOUND_WITH_ID = "Player not found with id: ";

    private final PlayerRepository playerRepository;

    public PlayerResponse registerPlayer(PlayerRegistrationRequest request) {

        if (!isAtLeast18YearsOld(request.getBirthdate())) {
            throw new UnderagePlayerException("Player must be at least 18 years old");
        }

        if (playerRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        Player player = Player.builder()
                .name(request.getName())
                .username(request.getUsername())
                .birthdate(request.getBirthdate())
                .balance(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .build();

        Player savedPlayer = playerRepository.save(player);
        return convertToResponse(savedPlayer);
    }

    public PlayerResponse getPlayer(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(PLAYER_NOT_FOUND_WITH_ID + id));
        return convertToResponse(player);
    }

    public PlayerResponse deposit(DepositRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(PLAYER_NOT_FOUND_WITH_ID + request.getPlayerId()));

        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        player.setBalance(player.getBalance().add(amount));
        playerRepository.save(player);

        return convertToResponse(player);
    }

    public void deductBalance(Long playerId, BigDecimal amount) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(PLAYER_NOT_FOUND_WITH_ID + playerId));

        BigDecimal scaledAmount = amount.setScale(2, RoundingMode.HALF_UP);

        if (player.getBalance().compareTo(scaledAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        player.setBalance(player.getBalance().subtract(scaledAmount));

        if (player.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Balance cannot be negative");
        }

        playerRepository.save(player);
    }

    public void addBalance(Long playerId, BigDecimal amount) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(PLAYER_NOT_FOUND_WITH_ID + playerId));

        BigDecimal scaledAmount = amount.setScale(2, RoundingMode.HALF_UP);
        player.setBalance(player.getBalance().add(scaledAmount));
        playerRepository.save(player);
    }

    public Player getPlayerEntity(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(PLAYER_NOT_FOUND_WITH_ID + id));
    }

    private boolean isAtLeast18YearsOld(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears() >= 18;
    }

    private PlayerResponse convertToResponse(Player player) {
        return PlayerResponse.builder()
                .id(player.getId())
                .name(player.getName())
                .username(player.getUsername())
                .birthdate(player.getBirthdate())
                .balance(player.getBalance())
                .build();
    }
}
