package com.omega.casino.config;

import com.omega.casino.service.GameService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameInitializer {

    private final GameService gameService;

    @PostConstruct
    public void init() {
        log.info("Initializing casino games...");
        gameService.initializeGames();
        log.info("Casino games initialized successfully");
    }
}
