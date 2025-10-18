package com.omega.casino.repository;

import com.omega.casino.entity.Bet;
import com.omega.casino.entity.BetResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BetRepositoryTest {

    private BetRepository betRepository;

    @BeforeEach
    void setUp() {
        betRepository = new BetRepository();
    }

    @Test
    void testSaveBet_GeneratesId() {
        Bet bet = Bet.builder()
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(10))
                .placedAt(LocalDateTime.now())
                .result(BetResult.WIN)
                .payout(BigDecimal.valueOf(20))
                .build();

        Bet savedBet = betRepository.save(bet);

        assertNotNull(savedBet.getId());
        assertEquals(1L, savedBet.getPlayerId());
    }

    @Test
    void testSaveBet_UpdatesExisting() {
        Bet bet = Bet.builder()
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(10))
                .placedAt(LocalDateTime.now())
                .result(BetResult.WIN)
                .payout(BigDecimal.valueOf(20))
                .build();

        Bet savedBet = betRepository.save(bet);
        Long betId = savedBet.getId();

        savedBet.setPayout(BigDecimal.valueOf(30));
        betRepository.save(savedBet);

        Optional<Bet> retrieved = betRepository.findById(betId);
        assertTrue(retrieved.isPresent());
        assertEquals(BigDecimal.valueOf(30), retrieved.get().getPayout());
    }

    @Test
    void testFindById_BetExists() {
        Bet bet = Bet.builder()
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(10))
                .placedAt(LocalDateTime.now())
                .result(BetResult.LOSE)
                .payout(BigDecimal.ZERO)
                .build();

        Bet savedBet = betRepository.save(bet);
        Optional<Bet> found = betRepository.findById(savedBet.getId());

        assertTrue(found.isPresent());
        assertEquals(BetResult.LOSE, found.get().getResult());
    }

    @Test
    void testFindById_BetNotExists() {
        Optional<Bet> found = betRepository.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void testIdGeneration_Unique() {
        Bet bet1 = Bet.builder()
                .playerId(1L)
                .gameId(1L)
                .betValue(BigDecimal.valueOf(10))
                .placedAt(LocalDateTime.now())
                .result(BetResult.WIN)
                .payout(BigDecimal.valueOf(20))
                .build();

        Bet bet2 = Bet.builder()
                .playerId(1L)
                .gameId(2L)
                .betValue(BigDecimal.valueOf(15))
                .placedAt(LocalDateTime.now())
                .result(BetResult.LOSE)
                .payout(BigDecimal.ZERO)
                .build();

        Bet saved1 = betRepository.save(bet1);
        Bet saved2 = betRepository.save(bet2);

        assertNotEquals(saved1.getId(), saved2.getId());
    }
}
