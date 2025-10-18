import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useGames } from '@/context/GameContext';
import { Game } from '@/types/game.types';
import { BET_AMOUNTS } from '@/utils/constants';
import { Button } from '@/components/common/Button';
import './GameContainer.css';

interface GameContainerProps {
  game: Game;
}

export function GameContainer({ game }: GameContainerProps) {
  const { state, placeBet } = useGames();
  const navigate = useNavigate();
  const [result, setResult] = useState<{ won: boolean; amount: number } | null>(null);
  const [isPlaying, setIsPlaying] = useState(false);

  const handleBet = (amount: number) => {
    if (amount > state.balance) {
      alert('Insufficient balance for this bet');
      return;
    }

    setIsPlaying(true);
    setResult(null);

    // Simulate game animation delay
    setTimeout(() => {
      const betResult = placeBet(amount);
      setResult({ won: betResult.won, amount: betResult.amount });
      setIsPlaying(false);
    }, 500);
  };

  return (
    <div className="game-container">
      <div className="game-play-area">
        <div className="game-image-container">
          <img src={game.imageUrl} alt={game.name} className="game-image" />
        </div>

        <div className="game-info">
          <h2 className="game-name">{game.name}</h2>
          {game.description && <p className="game-desc">{game.description}</p>}

          <div className="balance-display">
            <span className="balance-label">Your Balance:</span>
            <span className="balance-amount">${state.balance.toFixed(2)}</span>
          </div>

          {result && (
            <div className={`result-message ${result.won ? 'result-win' : 'result-lose'}`}>
              {result.won ? (
                <>
                  <span className="result-icon">🎉</span>
                  <span>You won ${result.amount.toFixed(2)}!</span>
                </>
              ) : (
                <>
                  <span className="result-icon">😞</span>
                  <span>You lost ${result.amount.toFixed(2)}</span>
                </>
              )}
            </div>
          )}

          <div className="bet-section">
            <h3>Place Your Bet</h3>
            <div className="bet-buttons">
              {BET_AMOUNTS.map((amount) => (
                <button
                  key={amount}
                  className="bet-button"
                  onClick={() => handleBet(amount)}
                  disabled={isPlaying || amount > state.balance}
                >
                  ${amount}
                </button>
              ))}
            </div>
            {isPlaying && <p className="playing-message">Playing...</p>}
          </div>

          <div className="game-actions">
            <Button variant="secondary" onClick={() => navigate('/games')}>
              Back to Library
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
