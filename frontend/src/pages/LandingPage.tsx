import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { Button } from '@/components/common/Button';
import { LoginDialog } from '@/components/auth/LoginDialog';
import { SignupDialog } from '@/components/auth/SignupDialog';
import './LandingPage.css';

export function LandingPage() {
  const { state } = useAuth();
  const [showLogin, setShowLogin] = useState(false);
  const [showSignup, setShowSignup] = useState(false);

  return (
    <div className="landing-page">
      <div className="landing-content">
        <div className="hero-section">
          <div className="hero-icon">🎰</div>
          <h1 className="hero-title">Welcome to Mini Casino</h1>
          <p className="hero-subtitle">
            {state.isAuthenticated
              ? `Welcome back, ${state.user?.username}! Ready to play?`
              : 'Your luck awaits! Sign up or log in to start playing.'}
          </p>

          <div className="hero-actions">
            {state.isAuthenticated ? (
              <Link to="/games">
                <Button variant="primary">Go to Games</Button>
              </Link>
            ) : (
              <>
                <Button variant="primary" onClick={() => setShowLogin(true)}>
                  Log In
                </Button>
                <Button variant="secondary" onClick={() => setShowSignup(true)}>
                  Sign Up
                </Button>
              </>
            )}
          </div>
        </div>

        <div className="features-section">
          <div className="feature">
            <div className="feature-icon">🎮</div>
            <h3>20+ Games</h3>
            <p>Choose from a variety of exciting casino games</p>
          </div>
          <div className="feature">
            <div className="feature-icon">💰</div>
            <h3>$100 Bonus</h3>
            <p>Start with $100 when you sign up</p>
          </div>
          <div className="feature">
            <div className="feature-icon">🎲</div>
            <h3>Fair Play</h3>
            <p>50/50 odds on every bet</p>
          </div>
        </div>
      </div>

      <LoginDialog
        isOpen={showLogin}
        onClose={() => setShowLogin(false)}
        onSwitchToSignup={() => setShowSignup(true)}
      />

      <SignupDialog
        isOpen={showSignup}
        onClose={() => setShowSignup(false)}
        onSwitchToLogin={() => setShowLogin(true)}
      />
    </div>
  );
}
