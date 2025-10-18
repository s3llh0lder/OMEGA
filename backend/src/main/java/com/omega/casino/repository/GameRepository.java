package com.omega.casino.repository;

import com.omega.casino.entity.Game;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class GameRepository {

    private final ConcurrentHashMap<Long, Game> games = new ConcurrentHashMap<>();

    public void saveAll(List<Game> gameList) {
        for (Game game : gameList) {
            games.put(game.getId(), game);
        }
    }

    public Optional<Game> findById(Long id) {
        return Optional.ofNullable(games.get(id));
    }

    public List<Game> findAll() {
        return new ArrayList<>(games.values());
    }
}
