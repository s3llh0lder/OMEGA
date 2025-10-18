package com.omega.casino.controller;

import com.omega.casino.dto.GameResponse;
import com.omega.casino.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@Tag(name = "Game Management", description = "APIs for viewing available games")
public class GameController {

    private final GameService gameService;

    @GetMapping
    @Operation(summary = "List all games", description = "Retrieve a list of all available casino games")
    public ResponseEntity<List<GameResponse>> getAllGames() {
        List<GameResponse> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get game details", description = "Retrieve game information by ID")
    public ResponseEntity<GameResponse> getGame(@PathVariable Long id) {
        GameResponse game = gameService.getGame(id);
        return ResponseEntity.ok(game);
    }
}
