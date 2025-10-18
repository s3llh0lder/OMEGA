import { ReactNode } from 'react';
import { AuthProvider } from './AuthContext';
import { GameProvider } from './GameContext';

/**
 * Combined context provider that wraps the entire app
 * Provides both Auth and Game contexts
 */
export function AppProvider({ children }: { children: ReactNode }) {
  return (
    <AuthProvider>
      <GameProvider>
        {children}
      </GameProvider>
    </AuthProvider>
  );
}
