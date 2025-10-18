/**
 * Save data to localStorage
 * @param key - Storage key
 * @param value - Value to store (will be JSON stringified)
 */
export function saveToStorage<T>(key: string, value: T): void {
  try {
    const serialized = JSON.stringify(value);
    localStorage.setItem(key, serialized);
  } catch (error) {
    console.error(`Error saving to localStorage (key: ${key}):`, error);
  }
}

/**
 * Load data from localStorage
 * @param key - Storage key
 * @returns Parsed value or null if not found or error
 */
export function loadFromStorage<T>(key: string): T | null {
  try {
    const serialized = localStorage.getItem(key);
    if (serialized === null) {
      return null;
    }
    return JSON.parse(serialized) as T;
  } catch (error) {
    console.error(`Error loading from localStorage (key: ${key}):`, error);
    return null;
  }
}

/**
 * Remove data from localStorage
 * @param key - Storage key
 */
export function clearStorage(key: string): void {
  try {
    localStorage.removeItem(key);
  } catch (error) {
    console.error(`Error clearing localStorage (key: ${key}):`, error);
  }
}

/**
 * Clear all app-related storage
 */
export function clearAllStorage(): void {
  try {
    const keys = Object.keys(localStorage);
    keys.forEach(key => {
      if (key.startsWith('mini-casino-')) {
        localStorage.removeItem(key);
      }
    });
  } catch (error) {
    console.error('Error clearing all storage:', error);
  }
}
