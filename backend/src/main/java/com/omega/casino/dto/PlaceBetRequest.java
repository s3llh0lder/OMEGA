package com.omega.casino.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceBetRequest {

    @NotNull(message = "Player ID is required")
    private Long playerId;

    @NotNull(message = "Game ID is required")
    private Long gameId;

    @NotNull(message = "Bet value is required")
    @DecimalMin(value = "0.01", message = "Bet value must be at least 0.01")
    private BigDecimal betValue;
}
