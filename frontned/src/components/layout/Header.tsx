import { Link } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { useGames } from '@/context/GameContext';
import { Button } from '@/components/common/Button';
import './Header.css';

export function Header() {
  const { state, logout } = useAuth();
  const { state: gameState } = useGames();

  return (
    <header className="header">
      <div className="header-container">
        <Link to="/" className="header-logo">
          <span className="logo-icon">🎰</span>
          <span className="logo-text">Mini Casino</span>
        </Link>

        <nav className="header-nav">
          {state.isAuthenticated ? (
            <>
              <Link to="/games" className="nav-link">
                Games
              </Link>
              <div className="user-info">
                <span className="username">{state.user?.username}</span>
                <span className="balance">
                  ${gameState.balance.toFixed(2)}
                </span>
              </div>
              <Button variant="secondary" onClick={logout}>
                Logout
              </Button>
            </>
          ) : null}
        </nav>
      </div>
    </header>
  );
}
