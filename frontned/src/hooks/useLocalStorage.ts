import { useState, useEffect } from 'react';
import { loadFromStorage, saveToStorage } from '@/utils/storage';

/**
 * Custom hook for managing localStorage state
 * @param key - Storage key
 * @param initialValue - Initial value if nothing in storage
 * @returns [value, setValue] tuple similar to useState
 */
export function useLocalStorage<T>(key: string, initialValue: T): [T, (value: T) => void] {
  // Initialize state with value from localStorage or initial value
  const [storedValue, setStoredValue] = useState<T>(() => {
    const item = loadFromStorage<T>(key);
    return item !== null ? item : initialValue;
  });

  // Update localStorage when state changes
  useEffect(() => {
    saveToStorage(key, storedValue);
  }, [key, storedValue]);

  return [storedValue, setStoredValue];
}
