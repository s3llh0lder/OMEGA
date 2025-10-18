import { User } from './user.types';

export interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
}

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface StoredAuth {
  isAuthenticated: boolean;
  username: string;
}
