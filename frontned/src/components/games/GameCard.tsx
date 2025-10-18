import { useNavigate } from 'react-router-dom';
import { Game } from '@/types/game.types';
import './GameCard.css';

interface GameCardProps {
  game: Game;
}

export function GameCard({ game }: GameCardProps) {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/games/${game.id}`);
  };

  return (
    <div className="game-card" onClick={handleClick}>
      <div className="game-card-image-wrapper">
        <img
          src={game.imageUrl}
          alt={game.name}
          className="game-card-image"
          loading="lazy"
        />
        <div className="game-card-overlay">
          <span className="game-card-play">Play Now</span>
        </div>
      </div>
      <div className="game-card-info">
        <h3 className="game-card-title">{game.name}</h3>
        {game.description && (
          <p className="game-card-description">{game.description}</p>
        )}
      </div>
    </div>
  );
}
