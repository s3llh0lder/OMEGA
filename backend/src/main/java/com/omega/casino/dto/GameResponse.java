package com.omega.casino.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {
    private Long id;
    private String name;
    private BigDecimal chanceOfWinning;
    private BigDecimal winningMultiplier;
    private BigDecimal maxBet;
    private BigDecimal minBet;
}
