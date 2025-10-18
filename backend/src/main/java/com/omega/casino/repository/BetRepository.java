package com.omega.casino.repository;

import com.omega.casino.entity.Bet;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class BetRepository {

    private final ConcurrentHashMap<Long, Bet> bets = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Bet save(Bet bet) {
        if (bet.getId() == null) {
            bet.setId(idGenerator.getAndIncrement());
        }
        bets.put(bet.getId(), bet);
        return bet;
    }

    public Optional<Bet> findById(Long id) {
        return Optional.ofNullable(bets.get(id));
    }
}
