package com.omega.casino.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bet {
    private Long id;
    private Long playerId;
    private Long gameId;
    private BigDecimal betValue;
    private LocalDateTime placedAt;
    private BetResult result;
    private BigDecimal payout;
}
