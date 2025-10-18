import { useEffect } from 'react';
import { useParams, Navigate } from 'react-router-dom';
import { useGames } from '@/context/GameContext';
import { GameContainer } from '@/components/games/GameContainer';

export function GamePlayPage() {
  const { gameId } = useParams<{ gameId: string }>();
  const { state, setCurrentGame } = useGames();

  useEffect(() => {
    if (gameId) {
      setCurrentGame(gameId);
    }
  }, [gameId, setCurrentGame]);

  // If no gameId or game not found, redirect to games library
  if (!gameId || !state.currentGame) {
    return <Navigate to="/games" replace />;
  }

  return <GameContainer game={state.currentGame} />;
}
