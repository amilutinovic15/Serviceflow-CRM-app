import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { createBrowserRouter, RouterProvider } from 'react-router'
import { AuthProvider } from './auth/AuthProvider'
import { RequireAuth } from './auth/RequireAuth'
import { AppLayout } from './components/AppLayout'
import './index.css'
import { ApiError } from './lib/api'
import { DashboardPage } from './pages/DashboardPage'
import { LoginPage } from './pages/LoginPage'
import { PlaceholderPage } from './pages/PlaceholderPage'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // Client errors (401/403/404) will not change on retry.
      retry: (failureCount, error) =>
        !(error instanceof ApiError && error.status >= 400 && error.status < 500) && failureCount < 2,
      refetchOnWindowFocus: false,
    },
  },
})

const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  {
    element: <RequireAuth />,
    children: [
      {
        element: <AppLayout />,
        children: [
          { index: true, element: <DashboardPage /> },
          { path: 'customers', element: <PlaceholderPage title="Klijenti" /> },
          { path: 'jobs', element: <PlaceholderPage title="Poslovi" /> },
          { path: 'invoices', element: <PlaceholderPage title="Fakture" /> },
          { path: 'assets', element: <PlaceholderPage title="Imovina i ugovori" /> },
          { path: 'catalog', element: <PlaceholderPage title="Katalog usluga" /> },
          { path: 'users', element: <PlaceholderPage title="Korisnici" /> },
          { path: 'settings', element: <PlaceholderPage title="Podešavanja" /> },
          { path: '*', element: <PlaceholderPage title="Stranica nije pronađena" /> },
        ],
      },
    ],
  },
])

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </QueryClientProvider>
  </StrictMode>,
)
