/// <reference types="vitest" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import flowbiteReact from "flowbite-react/plugin/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), flowbiteReact()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      }
    }
  },
  test: {
    environment: 'jsdom',
    setupFiles: 'src/test/setup.ts',
    css: true,
    globals: true,
    
    pool: 'forks',
    poolOptions: {
      forks: {
        singleFork: true
      }
    },
    exclude: [
      '**/node_modules/**',
      '**/node_modules/@mui/icons-material/**',
      '**/dist/**'
    ]
  }
})