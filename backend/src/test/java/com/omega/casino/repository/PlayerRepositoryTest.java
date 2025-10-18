package com.omega.casino.repository;

import com.omega.casino.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PlayerRepositoryTest {

    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        playerRepository = new PlayerRepository();
    }

    @Test
    void testSavePlayer_GeneratesId() {
        Player player = Player.builder()
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.ZERO)
                .build();

        Player savedPlayer = playerRepository.save(player);

        assertNotNull(savedPlayer.getId());
        assertEquals("testuser", savedPlayer.getUsername());
    }

    @Test
    void testSavePlayer_UpdatesExisting() {
        Player player = Player.builder()
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.ZERO)
                .build();

        Player savedPlayer = playerRepository.save(player);
        Long playerId = savedPlayer.getId();

        savedPlayer.setBalance(BigDecimal.valueOf(100));
        playerRepository.save(savedPlayer);

        Optional<Player> retrieved = playerRepository.findById(playerId);
        assertTrue(retrieved.isPresent());
        assertEquals(BigDecimal.valueOf(100), retrieved.get().getBalance());
    }

    @Test
    void testFindById_PlayerExists() {
        Player player = Player.builder()
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.ZERO)
                .build();

        Player savedPlayer = playerRepository.save(player);
        Optional<Player> found = playerRepository.findById(savedPlayer.getId());

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testFindById_PlayerNotExists() {
        Optional<Player> found = playerRepository.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUsername_PlayerExists() {
        Player player = Player.builder()
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.ZERO)
                .build();

        playerRepository.save(player);
        Optional<Player> found = playerRepository.findByUsername("testuser");

        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    void testFindByUsername_PlayerNotExists() {
        Optional<Player> found = playerRepository.findByUsername("nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByUsername_True() {
        Player player = Player.builder()
                .name("Test User")
                .username("testuser")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.ZERO)
                .build();

        playerRepository.save(player);
        assertTrue(playerRepository.existsByUsername("testuser"));
    }

    @Test
    void testExistsByUsername_False() {
        assertFalse(playerRepository.existsByUsername("nonexistent"));
    }

    @Test
    void testIdGeneration_Unique() {
        Player player1 = Player.builder()
                .name("User 1")
                .username("user1")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.ZERO)
                .build();

        Player player2 = Player.builder()
                .name("User 2")
                .username("user2")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(BigDecimal.ZERO)
                .build();

        Player saved1 = playerRepository.save(player1);
        Player saved2 = playerRepository.save(player2);

        assertNotEquals(saved1.getId(), saved2.getId());
    }
}
