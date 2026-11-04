import type { InvoiceStatus, ServiceJobPriority, ServiceJobStatus } from './types'

const LOCALE = 'sr-Latn-RS'

export function formatMoney(value: number | null | undefined, currency = 'RSD'): string {
  if (value == null) {
    return '—'
  }
  return new Intl.NumberFormat(LOCALE, { style: 'currency', currency, maximumFractionDigits: 2 }).format(value)
}

export function formatDate(value: string | null | undefined): string {
  if (!value) {
    return '—'
  }
  return new Intl.DateTimeFormat(LOCALE, { day: '2-digit', month: '2-digit', year: 'numeric' }).format(new Date(value))
}

export function formatDateTime(value: string | null | undefined): string {
  if (!value) {
    return '—'
  }
  return new Intl.DateTimeFormat(LOCALE, {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
}

export const jobStatusLabels: Record<ServiceJobStatus, string> = {
  NEW: 'Novo',
  SCHEDULED: 'Zakazano',
  IN_PROGRESS: 'U toku',
  COMPLETED: 'Završeno',
  PAID: 'Naplaćeno',
  CANCELLED: 'Otkazano',
}

export const jobPriorityLabels: Record<ServiceJobPriority, string> = {
  LOW: 'Nizak',
  NORMAL: 'Normalan',
  HIGH: 'Visok',
  URGENT: 'Hitno',
}

export const invoiceStatusLabels: Record<InvoiceStatus, string> = {
  DRAFT: 'Nacrt',
  SENT: 'Poslata',
  PAID: 'Plaćena',
  OVERDUE: 'Dospela',
  CANCELLED: 'Stornirana',
}
