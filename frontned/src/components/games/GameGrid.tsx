import { useState, useEffect } from 'react';
import { useGames } from '@/context/GameContext';
import { GameCard } from './GameCard';
import { Button } from '@/components/common/Button';
import './GameGrid.css';

export function GameGrid() {
  const { state, setSearchQuery, loadMoreGames, getFilteredGames } = useGames();
  const [searchInput, setSearchInput] = useState(state.searchQuery);

  // Sync local search input with context state when component mounts or context changes
  useEffect(() => {
    setSearchInput(state.searchQuery);
  }, [state.searchQuery]);

  const filteredGames = getFilteredGames();
  const visibleGames = filteredGames.slice(0, state.visibleGamesCount);
  const hasMoreGames = visibleGames.length < filteredGames.length;

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSearchInput(value);
    setSearchQuery(value);
  };

  return (
    <div className="game-grid-container">
      <div className="game-grid-header">
        <h2>Game Library</h2>
        <div className="search-box">
          <input
            type="text"
            placeholder="Search games..."
            value={searchInput}
            onChange={handleSearch}
            className="search-input"
          />
          <span className="search-icon">🔍</span>
        </div>
      </div>

      {filteredGames.length === 0 ? (
        <div className="no-games">
          <p>No games found matching "{state.searchQuery}"</p>
        </div>
      ) : (
        <>
          <div className="game-grid">
            {visibleGames.map((game) => (
              <GameCard key={game.id} game={game} />
            ))}
          </div>

          {hasMoreGames && (
            <div className="load-more-container">
              <Button onClick={loadMoreGames}>See More Games</Button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
