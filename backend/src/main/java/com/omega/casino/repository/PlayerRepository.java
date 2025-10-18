package com.omega.casino.repository;

import com.omega.casino.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PlayerRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerRepository.class);

    private final Map<Long, Player> players = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Player save(Player player) {
        if (player.getId() == null) {
            player.setId(idGenerator.getAndIncrement());
            LOGGER.info("New player registered with ID: {}", player.getId());
        }
        players.put(player.getId(), player);
        return player;
    }

    public Optional<Player> findById(Long id) {
        return Optional.ofNullable(players.get(id));
    }

    public Optional<Player> findByUsername(String username) {
        return players.values().stream()
                .filter(player -> player.getUsername().equals(username))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return players.values().stream()
                .anyMatch(player -> player.getUsername().equals(username));
    }
}
