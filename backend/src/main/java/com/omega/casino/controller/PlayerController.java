package com.omega.casino.controller;

import com.omega.casino.dto.DepositRequest;
import com.omega.casino.dto.PlayerRegistrationRequest;
import com.omega.casino.dto.PlayerResponse;
import com.omega.casino.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
@Tag(name = "Player Management", description = "APIs for player registration and management")
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping("/register")
    @Operation(summary = "Register a new player", description = "Register a new player with name, username, and birthdate. Player must be 18+ years old.")
    public ResponseEntity<PlayerResponse> registerPlayer(@Valid @RequestBody PlayerRegistrationRequest request) {
        PlayerResponse response = playerService.registerPlayer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get player details", description = "Retrieve player information by ID")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable Long id) {
        PlayerResponse response = playerService.getPlayer(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit money", description = "Deposit money into a player's account")
    public ResponseEntity<PlayerResponse> deposit(@Valid @RequestBody DepositRequest request) {
        PlayerResponse response = playerService.deposit(request);
        return ResponseEntity.ok(response);
    }
}
