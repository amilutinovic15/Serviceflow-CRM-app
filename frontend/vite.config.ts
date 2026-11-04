import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 5173,
    // In development the backend is reached through the dev server, so no CORS round trip is needed.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
