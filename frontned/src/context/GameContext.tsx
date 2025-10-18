import React, { createContext, useReducer, useEffect, useCallback, ReactNode } from 'react';
import { GameState, Game, BetResult } from '@/types/game.types';
import { mockGames } from '@/data/mockGames';
import { loadFromStorage, saveToStorage } from '@/utils/storage';
import { STORAGE_KEYS, INITIAL_BALANCE, GAMES_PER_PAGE } from '@/utils/constants';
import { getAllGames, placeBet as placeBetAPI, getPlayer, APIError } from '@/services/api';

type GameAction =
  | { type: 'LOAD_GAMES' }
  | { type: 'SET_GAMES'; payload: Game[] }
  | { type: 'SET_CURRENT_GAME'; payload: string }
  | { type: 'PLACE_BET'; payload: BetResult }
  | { type: 'SET_SEARCH_QUERY'; payload: string }
  | { type: 'LOAD_MORE_GAMES' }
  | { type: 'RESTORE_BALANCE'; payload: number };

interface GameContextValue {
  state: GameState;
  placeBet: (amount: number) => Promise<BetResult>;
  setCurrentGame: (gameId: string) => void;
  setSearchQuery: (query: string) => void;
  loadMoreGames: () => void;
  getFilteredGames: () => Game[];
}

const GameContext = createContext<GameContextValue | undefined>(undefined);

function gameReducer(state: GameState, action: GameAction): GameState {
  switch (action.type) {
    case 'LOAD_GAMES':
      return {
        ...state,
        games: mockGames,
      };
    case 'SET_GAMES':
      return {
        ...state,
        games: action.payload,
      };
    case 'SET_CURRENT_GAME':
      return {
        ...state,
        currentGame: state.games.find(game => game.id === action.payload) || null,
      };
    case 'PLACE_BET':
      return {
        ...state,
        balance: action.payload.newBalance,
      };
    case 'SET_SEARCH_QUERY':
      return {
        ...state,
        searchQuery: action.payload,
        visibleGamesCount: GAMES_PER_PAGE, // Reset visible count when searching
      };
    case 'LOAD_MORE_GAMES':
      return {
        ...state,
        visibleGamesCount: state.visibleGamesCount + GAMES_PER_PAGE,
      };
    case 'RESTORE_BALANCE':
      return {
        ...state,
        balance: action.payload,
      };
    default:
      return state;
  }
}

export function GameProvider({ children }: { children: ReactNode }) {
  // Initialize balance from localStorage to prevent flash of default value
  const storedBalance = loadFromStorage<number>(STORAGE_KEYS.BALANCE);
  const initialState: GameState = {
    games: [],
    balance: storedBalance ?? INITIAL_BALANCE,
    currentGame: null,
    visibleGamesCount: GAMES_PER_PAGE,
    searchQuery: '',
  };

  const [state, dispatch] = useReducer(gameReducer, initialState);

  // Load games from backend on mount
  useEffect(() => {
    const loadGames = async () => {
      try {
        const backendGames = await getAllGames();

        // Map backend games (with number IDs) to frontend Game format (with string IDs)
        const games: Game[] = backendGames.map(game => ({
          id: String(game.id),
          name: game.name,
          imageUrl: mockGames.find(mg => mg.name === game.name)?.imageUrl ||
                    'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400&h=400&fit=crop',
          description: game.description,
        }));

        dispatch({ type: 'SET_GAMES', payload: games });
      } catch (error) {
        console.error('Failed to load games from backend, using mock data:', error);

        // Display user-friendly error message
        if (error instanceof APIError) {
          alert(`Unable to load games: ${error.message}. Using offline mode.`);
        } else {
          alert('Unable to connect to game server. Using offline mode.');
        }

        // Fallback to mock games
        dispatch({ type: 'LOAD_GAMES' });
      }
    };

    loadGames();
  }, []);

  // Load player balance from backend on mount
  useEffect(() => {
    const loadBalance = async () => {
      const playerId = loadFromStorage<number>(STORAGE_KEYS.PLAYER_ID);

      if (!playerId) {
        return; // No player ID, keep localStorage balance
      }

      try {
        const player = await getPlayer(playerId);

        // Update balance from backend (source of truth)
        dispatch({ type: 'RESTORE_BALANCE', payload: player.balance });

        // Sync localStorage with backend balance
        saveToStorage(STORAGE_KEYS.BALANCE, player.balance);
      } catch (error) {
        console.error('Failed to load balance from backend:', error);

        // Display user-friendly error message
        if (error instanceof APIError) {
          alert(`Unable to sync balance: ${error.message}. Using local balance.`);
        } else {
          alert('Unable to sync balance with server. Using local balance.');
        }

        // Keep the localStorage balance as fallback
      }
    };

    loadBalance();
  }, []);

  // Save balance to localStorage whenever it changes
  useEffect(() => {
    saveToStorage(STORAGE_KEYS.BALANCE, state.balance);
  }, [state.balance]);

  const placeBet = useCallback(async (amount: number): Promise<BetResult> => {
    const playerId = loadFromStorage<number>(STORAGE_KEYS.PLAYER_ID);

    // Fallback to local calculation if no player ID or no current game
    if (!playerId || !state.currentGame) {
      const won = Math.random() >= 0.5;
      const payout = won ? amount * 2 : 0;
      const localResult: BetResult = {
        won,
        amount,
        payout,
        newBalance: state.balance - amount + payout,
      };
      dispatch({ type: 'PLACE_BET', payload: localResult });
      return localResult;
    }

    try {
      // Call backend API to place bet
      const gameId = Number(state.currentGame.id);
      const betResponse = await placeBetAPI({
        playerId,
        gameId,
        betValue: amount,
      });

      // Map backend response to frontend BetResult format
      const result: BetResult = {
        won: betResponse.result === 'WIN',
        amount: betResponse.betValue,
        payout: betResponse.payout,
        newBalance: betResponse.newBalance,
      };

      // Update local state with new balance
      dispatch({ type: 'PLACE_BET', payload: result });

      return result;
    } catch (error) {
      console.error('Failed to place bet via backend:', error);

      // Display user-friendly error message
      if (error instanceof APIError) {
        alert(`Unable to place bet: ${error.message}`);
        throw error; // Re-throw to prevent bet from being placed
      } else {
        alert('An unexpected error occurred while placing your bet. Please try again.');
        throw error;
      }
    }
  }, [state.currentGame, state.balance]);

  const setCurrentGame = useCallback((gameId: string): void => {
    dispatch({ type: 'SET_CURRENT_GAME', payload: gameId });
  }, []);

  const setSearchQuery = useCallback((query: string): void => {
    dispatch({ type: 'SET_SEARCH_QUERY', payload: query });
  }, []);

  const loadMoreGames = useCallback((): void => {
    dispatch({ type: 'LOAD_MORE_GAMES' });
  }, []);

  const getFilteredGames = useCallback((): Game[] => {
    if (!state.searchQuery.trim()) {
      return state.games;
    }

    const query = state.searchQuery.toLowerCase();
    return state.games.filter(game =>
      game.name.toLowerCase().includes(query)
    );
  }, [state.searchQuery, state.games]);

  const value: GameContextValue = {
    state,
    placeBet,
    setCurrentGame,
    setSearchQuery,
    loadMoreGames,
    getFilteredGames,
  };

  return <GameContext.Provider value={value}>{children}</GameContext.Provider>;
}

export function useGames(): GameContextValue {
  const context = React.useContext(GameContext);
  if (context === undefined) {
    throw new Error('useGames must be used within a GameProvider');
  }
  return context;
}
