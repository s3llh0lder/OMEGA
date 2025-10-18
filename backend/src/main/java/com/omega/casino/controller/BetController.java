package com.omega.casino.controller;

import com.omega.casino.dto.BetResponse;
import com.omega.casino.dto.PlaceBetRequest;
import com.omega.casino.service.BetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bets")
@RequiredArgsConstructor
@Tag(name = "Betting", description = "APIs for placing bets")
public class BetController {

    private final BetService betService;

    @PostMapping("/place")
    @Operation(summary = "Place a bet", description = "Place a bet on a game. The bet must be within the game's min and max bet limits, and the player must have sufficient balance.")
    public ResponseEntity<BetResponse> placeBet(@Valid @RequestBody PlaceBetRequest request) {
        BetResponse response = betService.placeBet(request);
        return ResponseEntity.ok(response);
    }
}
