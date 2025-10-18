import { differenceInYears } from 'date-fns';
import { MIN_AGE, MIN_PASSWORD_LENGTH, PASSWORD_REGEX } from './constants';

export interface ValidationResult {
  isValid: boolean;
  error?: string;
}

export interface PasswordValidation {
  minLength: boolean;
  hasSpecialChar: boolean;
  hasNumber: boolean;
  hasLetter: boolean;
}

export function validatePassword(password: string): PasswordValidation {
  return {
    minLength: password.length >= MIN_PASSWORD_LENGTH,
    hasSpecialChar: PASSWORD_REGEX.SPECIAL_CHAR.test(password),
    hasNumber: PASSWORD_REGEX.NUMBER.test(password),
    hasLetter: PASSWORD_REGEX.LETTER.test(password),
  };
}

export function isPasswordValid(validation: PasswordValidation): boolean {
  return (
    validation.minLength &&
    validation.hasSpecialChar &&
    validation.hasNumber &&
    validation.hasLetter
  );
}

export function validateAge(birthdate: string): boolean {
  if (!birthdate) return false;

  const age = differenceInYears(new Date(), new Date(birthdate));
  return age >= MIN_AGE;
}

export function validateUsername(username: string): ValidationResult {
  if (!username || username.trim().length === 0) {
    return { isValid: false, error: 'Username is required' };
  }

  if (username.length < 3) {
    return { isValid: false, error: 'Username must be at least 3 characters' };
  }

  return { isValid: true };
}

export function getPasswordErrorMessage(validation: PasswordValidation): string {
  const errors: string[] = [];

  if (!validation.minLength) {
    errors.push('at least 5 characters');
  }
  if (!validation.hasSpecialChar) {
    errors.push('1 special character');
  }
  if (!validation.hasNumber) {
    errors.push('1 number');
  }
  if (!validation.hasLetter) {
    errors.push('1 letter');
  }

  if (errors.length > 0) {
    return `Password must contain ${errors.join(', ')}`;
  }

  return '';
}
