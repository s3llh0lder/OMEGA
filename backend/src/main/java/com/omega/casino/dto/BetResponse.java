package com.omega.casino.dto;

import com.omega.casino.entity.BetResult;
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
public class BetResponse {
    private Long id;
    private Long playerId;
    private Long gameId;
    private String gameName;
    private BigDecimal betValue;
    private LocalDateTime placedAt;
    private BetResult result;
    private BigDecimal payout;
    private BigDecimal newBalance;
}
