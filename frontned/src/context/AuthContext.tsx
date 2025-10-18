import React, { createContext, useReducer, useEffect, ReactNode } from 'react';
import { AuthState, LoginCredentials, StoredAuth } from '@/types/auth.types';
import { User, SignupData } from '@/types/user.types';
import { loadFromStorage, saveToStorage, clearStorage } from '@/utils/storage';
import { STORAGE_KEYS, INITIAL_BALANCE } from '@/utils/constants';
import { validatePassword, validateAge, isPasswordValid } from '@/utils/validation';
import { registerPlayer, depositMoney, APIError } from '@/services/api';

type AuthAction =
  | { type: 'LOGIN'; payload: User }
  | { type: 'LOGOUT' }
  | { type: 'RESTORE_SESSION'; payload: User };

interface AuthContextValue {
  state: AuthState;
  login: (credentials: LoginCredentials) => Promise<void>;
  signup: (data: SignupData) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function authReducer(state: AuthState, action: AuthAction): AuthState {
  switch (action.type) {
    case 'LOGIN':
    case 'RESTORE_SESSION':
      return {
        isAuthenticated: true,
        user: action.payload,
      };
    case 'LOGOUT':
      return {
        isAuthenticated: false,
        user: null,
      };
    default:
      return state;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const initialState: AuthState = {
    isAuthenticated: false,
    user: null,
  };

  const [state, dispatch] = useReducer(authReducer, initialState);

  // Restore session on mount
  useEffect(() => {
    const storedAuth = loadFromStorage<StoredAuth>(STORAGE_KEYS.AUTH);
    if (storedAuth?.isAuthenticated && storedAuth.username) {
      const user = loadFromStorage<User>(STORAGE_KEYS.USER);
      if (user && user.username === storedAuth.username) {
        dispatch({ type: 'RESTORE_SESSION', payload: user });
      }
    }
  }, []);

  // Save auth state to localStorage
  useEffect(() => {
    if (state.isAuthenticated && state.user) {
      const storedAuth: StoredAuth = {
        isAuthenticated: true,
        username: state.user.username,
      };
      saveToStorage(STORAGE_KEYS.AUTH, storedAuth);
    } else {
      clearStorage(STORAGE_KEYS.AUTH);
    }
  }, [state.isAuthenticated, state.user]);

  const login = async (credentials: LoginCredentials): Promise<void> => {
    try {
      // Load user from storage
      const storedUser = loadFromStorage<User>(STORAGE_KEYS.USER);

      if (!storedUser) {
        const errorMessage = 'User not found. Please sign up first.';
        alert(errorMessage);
        throw new Error(errorMessage);
      }

      // Validate credentials
      if (storedUser.username !== credentials.username || storedUser.password !== credentials.password) {
        const errorMessage = 'Invalid username or password';
        alert(errorMessage);
        throw new Error(errorMessage);
      }

      // Login successful
      dispatch({ type: 'LOGIN', payload: storedUser });
    } catch (error) {
      // If error was already handled above, just re-throw
      if (error instanceof Error && (error.message === 'User not found. Please sign up first.' || error.message === 'Invalid username or password')) {
        throw error;
      }

      // Handle unexpected errors
      alert('An unexpected error occurred during login. Please try again.');
      throw error;
    }
  };

  const signup = async (data: SignupData): Promise<void> => {
    // Check if user already exists
    const existingUser = loadFromStorage<User>(STORAGE_KEYS.USER);
    if (existingUser && existingUser.username === data.username) {
      throw new Error('Username already exists');
    }

    // Validate password
    const passwordValidation = validatePassword(data.password);
    if (!isPasswordValid(passwordValidation)) {
      throw new Error('Password must contain at least 5 characters, 1 special character, 1 number, and 1 letter');
    }

    // Validate password match
    if (data.password !== data.repeatPassword) {
      throw new Error('Passwords do not match');
    }

    // Validate age
    if (!validateAge(data.birthdate)) {
      throw new Error('You must be at least 18 years old');
    }

    try {
      // Register player with backend API
      const playerResponse = await registerPlayer({
        name: data.username, // Using username as name
        username: data.username,
        birthdate: data.birthdate,
      });

      // Deposit initial balance
      await depositMoney({
        playerId: playerResponse.id,
        amount: INITIAL_BALANCE,
      });

      // Store player ID for future API calls
      saveToStorage(STORAGE_KEYS.PLAYER_ID, playerResponse.id);

      // Create new user object for local storage
      const newUser: User = {
        username: data.username,
        password: data.password, // In production, this would be hashed
        birthdate: data.birthdate,
        createdAt: new Date().toISOString(),
      };

      // Save user to storage
      saveToStorage(STORAGE_KEYS.USER, newUser);

      // Initialize balance in localStorage
      saveToStorage(STORAGE_KEYS.BALANCE, INITIAL_BALANCE);

      // Login the new user
      dispatch({ type: 'LOGIN', payload: newUser });
    } catch (error) {
      console.error('Backend API error during signup:', error);

      // Display user-friendly error message
      if (error instanceof APIError) {
        alert(error.message);
        throw error; // Re-throw to prevent signup from continuing
      } else {
        alert('An unexpected error occurred during signup. Please try again.');
        throw error;
      }
    }
  };

  const logout = (): void => {
    // Clear auth state but keep user data for re-login
    clearStorage(STORAGE_KEYS.AUTH);
    clearStorage(STORAGE_KEYS.LAST_PAGE);
    dispatch({ type: 'LOGOUT' });
  };

  const value: AuthContextValue = {
    state,
    login,
    signup,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = React.useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
