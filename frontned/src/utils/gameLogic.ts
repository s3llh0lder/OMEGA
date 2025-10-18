import { BetResult } from '@/types/game.types';

/**
 * Calculates the result of a bet with 50/50 random win/lose logic
 * @param betAmount - The amount being bet
 * @param currentBalance - The current balance
 * @returns BetResult with win status and new balance
 */
export function calculateBetResult(betAmount: number, currentBalance: number): BetResult {
  // Check if player has sufficient balance
  if (betAmount > currentBalance) {
    return {
      won: false,
      amount: betAmount,
      newBalance: currentBalance,
    };
  }

  // 50/50 random win/lose
  const won = Math.random() >= 0.5;

  // Calculate new balance
  const newBalance = won
    ? currentBalance + betAmount  // Win: add bet amount (bet x 2 total, so net gain is bet amount)
    : currentBalance - betAmount; // Lose: deduct bet amount

  return {
    won,
    amount: betAmount,
    newBalance,
  };
}
