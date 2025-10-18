export interface Game {
  id: string;
  name: string;
  imageUrl: string; // Static image path
  description?: string;
}

export interface BetResult {
  won: boolean;
  amount: number;
  newBalance: number;
}

export interface GameState {
  games: Game[];
  balance: number;
  currentGame: Game | null;
  visibleGamesCount: number; // For "See More" functionality
  searchQuery: string;
}
