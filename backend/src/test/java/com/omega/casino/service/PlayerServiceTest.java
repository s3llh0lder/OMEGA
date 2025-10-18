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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    @Test
    void testRegisterPlayer_Success() {
        PlayerRegistrationRequest request = PlayerRegistrationRequest.builder()
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        Player savedPlayer = Player.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.ZERO)
                .build();

        when(playerRepository.existsByUsername("testuser")).thenReturn(false);
        when(playerRepository.save(any(Player.class))).thenReturn(savedPlayer);

        PlayerResponse response = playerService.registerPlayer(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals(BigDecimal.ZERO, response.getBalance());
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void testRegisterPlayer_UnderAge() {
        PlayerRegistrationRequest request = PlayerRegistrationRequest.builder()
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.now().minusYears(17))
                .build();

        assertThrows(UnderagePlayerException.class, () -> playerService.registerPlayer(request));
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testRegisterPlayer_UsernameExists() {
        PlayerRegistrationRequest request = PlayerRegistrationRequest.builder()
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        when(playerRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> playerService.registerPlayer(request));
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testGetPlayer_Success() {
        Player player = Player.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.valueOf(100))
                .build();

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        PlayerResponse response = playerService.getPlayer(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(BigDecimal.valueOf(100), response.getBalance());
    }

    @Test
    void testGetPlayer_NotFound() {
        when(playerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class, () -> playerService.getPlayer(999L));
    }

    @Test
    void testDeposit_Success() {
        DepositRequest request = DepositRequest.builder()
                .playerId(1L)
                .amount(BigDecimal.valueOf(50))
                .build();

        Player player = Player.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.valueOf(100))
                .build();

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        PlayerResponse response = playerService.deposit(request);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(150.00).setScale(2), response.getBalance().setScale(2));
    }

    @Test
    void testDeductBalance_Success() {
        Player player = Player.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.valueOf(100))
                .build();

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        playerService.deductBalance(1L, BigDecimal.valueOf(30));

        assertEquals(BigDecimal.valueOf(70.00).setScale(2), player.getBalance().setScale(2));
    }

    @Test
    void testDeductBalance_InsufficientBalance() {
        Player player = Player.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.valueOf(50))
                .build();

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        assertThrows(InsufficientBalanceException.class,
                () -> playerService.deductBalance(1L, BigDecimal.valueOf(100)));
    }

    @Test
    void testAddBalance_Success() {
        Player player = Player.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.valueOf(100))
                .build();

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        playerService.addBalance(1L, BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150.00).setScale(2), player.getBalance().setScale(2));
    }
}
