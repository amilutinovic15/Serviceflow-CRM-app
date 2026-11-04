# ServiceFlow frontend

React 19 + TypeScript + Vite, TanStack Query, React Router and Tailwind CSS.

## Local development

Start the backend on `http://localhost:8080` (see the root README), then:

```bash
npm install
npm run dev
```

Open `http://localhost:5173`. The Vite dev server proxies `/api` to the backend, so no CORS setup is needed locally.

For a separately hosted backend set `VITE_API_URL` (for example `https://api.example.com`) and add the frontend
origin to `SERVICEFLOW_CORS_ALLOWED_ORIGINS` on the backend.

## Structure

```text
src/auth        login state, JWT storage, protected routes
src/lib         API client, DTO types, formatting, shared queries
src/components  layout and shared UI
src/pages       one file per screen
```
