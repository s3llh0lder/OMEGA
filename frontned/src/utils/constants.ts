export const BET_AMOUNTS = [1, 3, 5, 10] as const;
export const INITIAL_BALANCE = 100;
export const GAMES_PER_PAGE = 8;
export const GAMES_PER_ROW = 4;
export const MIN_AGE = 18;
export const MIN_PASSWORD_LENGTH = 5;

export const PASSWORD_REGEX = {
  SPECIAL_CHAR: /[!@#$%^&*(),.?":{}|<>]/,
  NUMBER: /\d/,
  LETTER: /[a-zA-Z]/
};

export const STORAGE_KEYS = {
  AUTH: 'mini-casino-auth',
  BALANCE: 'mini-casino-balance',
  USER: 'mini-casino-user',
  LAST_PAGE: 'mini-casino-last-page',
  PLAYER_ID: 'mini-casino-player-id'
} as const;
