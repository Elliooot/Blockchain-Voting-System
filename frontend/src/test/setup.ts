/// <reference types="vitest" />
import '@testing-library/jest-dom';
import { vi } from 'vitest';

// JSDOM polyfills
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: (query: string) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: () => {},
    removeListener: () => {},
    addEventListener: () => {},
    removeEventListener: () => {},
    dispatchEvent: () => false,
  }),
});

// Import the mock module once (synchronously)
import * as MuiIconsMock from '../test/mui';

// Mock @mui/icons-material with our lightweight mock module
vi.mock('@mui/icons-material', () => ({
  ...MuiIconsMock,
  default: MuiIconsMock.MockIcon,
}));

// Mock specific icon paths to default-export a MockIcon
vi.mock('@mui/icons-material/Menu', () => ({ default: MuiIconsMock.Menu }));

// Block ESM icon entrypoints entirely (avoid touching the filesystem)
vi.mock('@mui/icons-material/esm/*', () => ({}));

// Silence noisy console during tests
vi.spyOn(console, 'log').mockImplementation(() => {});
vi.spyOn(console, 'warn').mockImplementation(() => {});

// Test runner config
vi.setConfig({
  testTimeout: 10000,
});