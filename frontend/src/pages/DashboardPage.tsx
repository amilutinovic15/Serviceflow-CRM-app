import { useQuery } from '@tanstack/react-query'
import type { ReactNode } from 'react'
import { StatusBadge, type BadgeTone } from '../components/StatusBadge'
import { api } from '../lib/api'
import {
  formatDate,
  formatDateTime,
  formatMoney,
  invoiceStatusLabels,
  jobPriorityLabels,
  jobStatusLabels,
} from '../lib/format'
import { useBusinessProfile } from '../lib/queries'
import type { DashboardSummary, InvoiceStatus, ServiceJobPriority } from '../lib/types'

const priorityTones: Record<ServiceJobPriority, BadgeTone> = {
  LOW: 'slate',
  NORMAL: 'blue',
  HIGH: 'amber',
  URGENT: 'red',
}

const invoiceTones: Record<InvoiceStatus, BadgeTone> = {
  DRAFT: 'slate',
  SENT: 'blue',
  PAID: 'green',
  OVERDUE: 'red',
  CANCELLED: 'slate',
}

export function DashboardPage() {
  const { data: profile } = useBusinessProfile()
  const { data, isPending, error } = useQuery({
    queryKey: ['dashboard'],
    queryFn: () => api.get<DashboardSummary>('/api/dashboard/summary'),
  })

  const currency = profile?.currency ?? 'RSD'

  if (isPending) {
    return <p className="text-sm text-slate-500">Učitavanje pregleda…</p>
  }

  if (error) {
    return <p className="rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">{error.message}</p>
  }

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-semibold">Pregled</h1>

      <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <StatCard label="Prihod ovog meseca" value={formatMoney(data.revenueThisMonth, currency)} />
        <StatCard label="Nenaplaćeno" value={formatMoney(data.outstandingTotal, currency)} />
        <StatCard
          label="Dospele fakture"
          value={String(data.overdueInvoicesCount)}
          highlight={data.overdueInvoicesCount > 0}
        />
        <StatCard label="Hitni poslovi" value={String(data.urgentJobsCount)} highlight={data.urgentJobsCount > 0} />
      </section>

      <section className="grid gap-4 sm:grid-cols-3">
        <StatCard label="Klijenti" value={String(data.totalCustomers)} small />
        <StatCard label="Poslovi" value={String(data.totalJobs)} small />
        <StatCard label="Fakturisano ukupno" value={formatMoney(data.invoicedTotal, currency)} small />
      </section>

      <div className="grid gap-6 xl:grid-cols-2">
        <Panel title="Predstojeći poslovi">
          {data.upcomingJobs.length === 0 ? (
            <Empty text="Nema zakazanih poslova." />
          ) : (
            <ul className="divide-y divide-slate-100">
              {data.upcomingJobs.map((job) => (
                <li key={job.id} className="flex items-center justify-between gap-4 px-5 py-3">
                  <div className="min-w-0">
                    <div className="truncate text-sm font-medium">{job.title}</div>
                    <div className="truncate text-xs text-slate-500">
                      {job.customerName} · {formatDateTime(job.scheduledAt)}
                      {job.assigneeName ? ` · ${job.assigneeName}` : ''}
                    </div>
                  </div>
                  <div className="flex shrink-0 gap-1.5">
                    <StatusBadge label={jobStatusLabels[job.status]} />
                    <StatusBadge label={jobPriorityLabels[job.priority]} tone={priorityTones[job.priority]} />
                  </div>
                </li>
              ))}
            </ul>
          )}
        </Panel>

        <Panel title="Nenaplaćene fakture">
          {data.unpaidInvoices.length === 0 ? (
            <Empty text="Sve fakture su naplaćene." />
          ) : (
            <ul className="divide-y divide-slate-100">
              {data.unpaidInvoices.map((invoice) => (
                <li key={invoice.id} className="flex items-center justify-between gap-4 px-5 py-3">
                  <div className="min-w-0">
                    <div className="truncate text-sm font-medium">
                      {invoice.invoiceNumber} · {invoice.customerName}
                    </div>
                    <div className="text-xs text-slate-500">Dospeva {formatDate(invoice.dueAt)}</div>
                  </div>
                  <div className="flex shrink-0 items-center gap-3">
                    <span className="text-sm font-medium tabular-nums">
                      {formatMoney(invoice.outstandingAmount, currency)}
                    </span>
                    <StatusBadge label={invoiceStatusLabels[invoice.status]} tone={invoiceTones[invoice.status]} />
                  </div>
                </li>
              ))}
            </ul>
          )}
        </Panel>
      </div>
    </div>
  )
}

function StatCard({
  label,
  value,
  highlight = false,
  small = false,
}: {
  label: string
  value: string
  highlight?: boolean
  small?: boolean
}) {
  return (
    <div className="rounded-xl border border-slate-200 bg-white p-5">
      <div className="text-sm text-slate-500">{label}</div>
      <div
        className={`mt-1 font-semibold tabular-nums ${small ? 'text-lg' : 'text-2xl'} ${
          highlight ? 'text-red-600' : 'text-slate-900'
        }`}
      >
        {value}
      </div>
    </div>
  )
}

function Panel({ title, children }: { title: string; children: ReactNode }) {
  return (
    <section className="overflow-hidden rounded-xl border border-slate-200 bg-white">
      <h2 className="border-b border-slate-200 px-5 py-3 text-sm font-semibold">{title}</h2>
      {children}
    </section>
  )
}

function Empty({ text }: { text: string }) {
  return <p className="px-5 py-8 text-center text-sm text-slate-500">{text}</p>
}
