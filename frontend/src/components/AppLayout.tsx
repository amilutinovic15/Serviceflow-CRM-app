import { useState } from 'react'
import { NavLink, Outlet } from 'react-router'
import { useAuth } from '../auth/useAuth'
import { useBusinessProfile } from '../lib/queries'

interface NavItem {
  to: string
  label: string
  adminOnly?: boolean
}

const navItems: NavItem[] = [
  { to: '/', label: 'Pregled' },
  { to: '/customers', label: 'Klijenti' },
  { to: '/jobs', label: 'Poslovi' },
  { to: '/invoices', label: 'Fakture' },
  { to: '/assets', label: 'Imovina i ugovori' },
  { to: '/catalog', label: 'Katalog usluga' },
  { to: '/users', label: 'Korisnici', adminOnly: true },
  { to: '/settings', label: 'Podešavanja' },
]

export function AppLayout() {
  const { user, logout } = useAuth()
  const { data: profile } = useBusinessProfile()
  const [menuOpen, setMenuOpen] = useState(false)

  const visibleItems = navItems.filter((item) => !item.adminOnly || user?.role === 'ADMIN')

  return (
    <div className="flex min-h-full">
      <aside
        className={`fixed inset-y-0 left-0 z-30 w-60 transform border-r border-slate-200 bg-white transition-transform md:static md:translate-x-0 ${
          menuOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex h-16 items-center gap-2 border-b border-slate-200 px-5">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-brand-600 text-sm font-bold text-white">
            SF
          </div>
          <div className="min-w-0">
            <div className="text-sm font-semibold">ServiceFlow</div>
            <div className="truncate text-xs text-slate-500">{profile?.businessName ?? ' '}</div>
          </div>
        </div>
        <nav className="flex flex-col gap-0.5 p-3">
          {visibleItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              onClick={() => setMenuOpen(false)}
              className={({ isActive }) =>
                `rounded-md px-3 py-2 text-sm font-medium ${
                  isActive ? 'bg-brand-50 text-brand-700' : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                }`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      {menuOpen && (
        <div className="fixed inset-0 z-20 bg-slate-900/30 md:hidden" onClick={() => setMenuOpen(false)} />
      )}

      <div className="flex min-w-0 flex-1 flex-col">
        <header className="flex h-16 items-center justify-between border-b border-slate-200 bg-white px-4 md:px-8">
          <button
            type="button"
            className="rounded-md p-2 text-slate-600 hover:bg-slate-100 md:hidden"
            onClick={() => setMenuOpen(true)}
            aria-label="Otvori meni"
          >
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.8">
              <path d="M3 5h14M3 10h14M3 15h14" strokeLinecap="round" />
            </svg>
          </button>
          <div className="ml-auto flex items-center gap-4">
            <div className="text-right">
              <div className="text-sm font-medium">{user?.fullName}</div>
              <div className="text-xs text-slate-500">{user?.role === 'ADMIN' ? 'Administrator' : 'Zaposleni'}</div>
            </div>
            <button
              type="button"
              onClick={logout}
              className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 hover:bg-slate-50"
            >
              Odjava
            </button>
          </div>
        </header>
        <main className="flex-1 p-4 md:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
