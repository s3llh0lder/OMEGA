import { useEffect, useState } from 'react';
import { useParams, Navigate } from 'react-router-dom';
import { useGames } from '@/context/GameContext';
import { GameContainer } from '@/components/games/GameContainer';

export function GamePlayPage() {
  const { gameId } = useParams<{ gameId: string }>();
  const { state, setCurrentGame } = useGames();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (gameId) {
      setCurrentGame(gameId);
      // Give the state time to update before rendering
      setIsLoading(false);
    } else {
      setIsLoading(false);
    }
  }, [gameId, setCurrentGame]);

  // If no gameId, redirect to games library
  if (!gameId) {
    return <Navigate to="/games" replace />;
  }

  // Wait for the game to be loaded before checking if it exists
  if (isLoading) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '400px',
        fontSize: '18px',
        color: '#666'
      }}>
        Loading game...
      </div>
    );
  }

  // If game not found after loading, redirect to games library
  if (!state.currentGame) {
    return <Navigate to="/games" replace />;
  }

  return <GameContainer game={state.currentGame} />;
}
