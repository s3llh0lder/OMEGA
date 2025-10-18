export interface User {
  username: string;
  password: string; // In real app, would be hashed
  birthdate: string; // ISO date string
  createdAt: string; // ISO date string
}

export interface SignupData {
  username: string;
  password: string;
  repeatPassword: string;
  birthdate: string;
}
