/**
 * Backend API Service Layer
 * Handles all HTTP communication with the Spring Boot backend
 */

const API_BASE_URL = 'http://localhost:8080/api';

// ============================================================================
// Type Definitions for Backend API
// ============================================================================

export interface RegisterPlayerRequest {
  name: string;
  username: string;
  birthdate: string; // ISO date string
}

export interface RegisterPlayerResponse {
  id: number;
  name: string;
  username: string;
  birthdate: string;
  balance: number;
}

export interface DepositRequest {
  playerId: number;
  amount: number;
}

export interface DepositResponse {
  id: number;
  balance: number;
}

export interface PlaceBetRequest {
  playerId: number;
  gameId: number;
  betValue: number;
}

export interface PlaceBetResponse {
  id: number;
  playerId: number;
  gameId: number;
  gameName: string;
  betValue: number;
  result: 'WIN' | 'LOSE';
  payout: number;
  newBalance: number;
}

export interface GameResponse {
  id: number;
  name: string;
  description?: string;
}

export interface PlayerResponse {
  id: number;
  name: string;
  username: string;
  birthdate: string;
  balance: number;
}

// ============================================================================
// Error Handling
// ============================================================================

export class APIError extends Error {
  constructor(
    message: string,
    public status?: number,
    public statusText?: string
  ) {
    super(message);
    this.name = 'APIError';
  }
}

// ============================================================================
// Generic Fetch Wrapper
// ============================================================================

async function fetchAPI<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;

  const config: RequestInit = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  };

  try {
    const response = await fetch(url, config);

    if (!response.ok) {
      let errorMessage = `API Error: ${response.status} ${response.statusText}`;

      try {
        const errorData = await response.json();
        // Backend returns { status, message, timestamp } format
        if (errorData && typeof errorData.message === 'string') {
          errorMessage = errorData.message;
        }
      } catch {
        // If response body is not JSON, use status text
      }

      throw new APIError(errorMessage, response.status, response.statusText);
    }

    // Handle empty responses
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
      return {} as T;
    }

    return await response.json();
  } catch (error) {
    if (error instanceof APIError) {
      throw error;
    }

    // Network error or other fetch errors
    throw new APIError(
      `Network error: ${error instanceof Error ? error.message : 'Unknown error'}`
    );
  }
}

// ============================================================================
// Player Management API
// ============================================================================

/**
 * Register a new player
 */
export async function registerPlayer(
  data: RegisterPlayerRequest
): Promise<RegisterPlayerResponse> {
  return fetchAPI<RegisterPlayerResponse>('/players/register', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

/**
 * Get player details by ID
 */
export async function getPlayer(playerId: number): Promise<PlayerResponse> {
  return fetchAPI<PlayerResponse>(`/players/${playerId}`);
}

/**
 * Deposit money to player account
 */
export async function depositMoney(
  data: DepositRequest
): Promise<DepositResponse> {
  return fetchAPI<DepositResponse>('/players/deposit', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

// ============================================================================
// Games API
// ============================================================================

/**
 * Get all available games
 */
export async function getAllGames(): Promise<GameResponse[]> {
  return fetchAPI<GameResponse[]>('/games');
}

/**
 * Get a specific game by ID
 */
export async function getGame(gameId: number): Promise<GameResponse> {
  return fetchAPI<GameResponse>(`/games/${gameId}`);
}

// ============================================================================
// Betting API
// ============================================================================

/**
 * Place a bet on a game
 */
export async function placeBet(
  data: PlaceBetRequest
): Promise<PlaceBetResponse> {
  return fetchAPI<PlaceBetResponse>('/bets/place', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}
