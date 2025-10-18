import React, { createContext, useReducer, useEffect, ReactNode } from 'react';
import { AuthState, LoginCredentials, StoredAuth } from '@/types/auth.types';
import { User, SignupData } from '@/types/user.types';
import { loadFromStorage, saveToStorage, clearStorage } from '@/utils/storage';
import { STORAGE_KEYS, INITIAL_BALANCE } from '@/utils/constants';
import { validatePassword, validateAge, isPasswordValid } from '@/utils/validation';

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
    // Load user from storage
    const storedUser = loadFromStorage<User>(STORAGE_KEYS.USER);

    if (!storedUser) {
      throw new Error('User not found. Please sign up first.');
    }

    // Validate credentials
    if (storedUser.username !== credentials.username || storedUser.password !== credentials.password) {
      throw new Error('Invalid username or password');
    }

    // Login successful
    dispatch({ type: 'LOGIN', payload: storedUser });
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

    // Create new user
    const newUser: User = {
      username: data.username,
      password: data.password, // In production, this would be hashed
      birthdate: data.birthdate,
      createdAt: new Date().toISOString(),
    };

    // Save user to storage
    saveToStorage(STORAGE_KEYS.USER, newUser);

    // Initialize balance
    saveToStorage(STORAGE_KEYS.BALANCE, INITIAL_BALANCE);

    // Login the new user
    dispatch({ type: 'LOGIN', payload: newUser });
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
