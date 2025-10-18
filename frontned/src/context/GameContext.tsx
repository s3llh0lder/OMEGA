import React, { createContext, useReducer, useEffect, ReactNode } from 'react';
import { GameState, Game, BetResult } from '@/types/game.types';
import { mockGames } from '@/data/mockGames';
import { calculateBetResult } from '@/utils/gameLogic';
import { loadFromStorage, saveToStorage } from '@/utils/storage';
import { STORAGE_KEYS, INITIAL_BALANCE, GAMES_PER_PAGE } from '@/utils/constants';

type GameAction =
  | { type: 'LOAD_GAMES' }
  | { type: 'SET_CURRENT_GAME'; payload: string }
  | { type: 'PLACE_BET'; payload: BetResult }
  | { type: 'SET_SEARCH_QUERY'; payload: string }
  | { type: 'LOAD_MORE_GAMES' }
  | { type: 'RESTORE_BALANCE'; payload: number };

interface GameContextValue {
  state: GameState;
  placeBet: (amount: number) => BetResult;
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
  const initialState: GameState = {
    games: [],
    balance: INITIAL_BALANCE,
    currentGame: null,
    visibleGamesCount: GAMES_PER_PAGE,
    searchQuery: '',
  };

  const [state, dispatch] = useReducer(gameReducer, initialState);

  // Load games on mount
  useEffect(() => {
    dispatch({ type: 'LOAD_GAMES' });

    // Restore balance from storage
    const storedBalance = loadFromStorage<number>(STORAGE_KEYS.BALANCE);
    if (storedBalance !== null) {
      dispatch({ type: 'RESTORE_BALANCE', payload: storedBalance });
    }
  }, []);

  // Save balance to localStorage whenever it changes
  useEffect(() => {
    saveToStorage(STORAGE_KEYS.BALANCE, state.balance);
  }, [state.balance]);

  const placeBet = (amount: number): BetResult => {
    const result = calculateBetResult(amount, state.balance);
    dispatch({ type: 'PLACE_BET', payload: result });
    return result;
  };

  const setCurrentGame = (gameId: string): void => {
    dispatch({ type: 'SET_CURRENT_GAME', payload: gameId });
  };

  const setSearchQuery = (query: string): void => {
    dispatch({ type: 'SET_SEARCH_QUERY', payload: query });
  };

  const loadMoreGames = (): void => {
    dispatch({ type: 'LOAD_MORE_GAMES' });
  };

  const getFilteredGames = (): Game[] => {
    if (!state.searchQuery.trim()) {
      return state.games;
    }

    const query = state.searchQuery.toLowerCase();
    return state.games.filter(game =>
      game.name.toLowerCase().includes(query)
    );
  };

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
