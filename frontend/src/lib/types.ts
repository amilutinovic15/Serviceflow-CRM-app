// Mirrors the backend DTOs in src/main/java/com/serviceflow.

export type UserRole = 'ADMIN' | 'STAFF'

export interface User {
  id: number
  fullName: string
  email: string
  role: UserRole
  enabled: boolean
  createdAt: string
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
  expiresAt: string
  user: User
}

export interface ApiErrorBody {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  fieldErrors: Record<string, string>
}

export type ServiceJobStatus = 'NEW' | 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'PAID' | 'CANCELLED'
export type ServiceJobPriority = 'LOW' | 'NORMAL' | 'HIGH' | 'URGENT'
export type InvoiceStatus = 'DRAFT' | 'SENT' | 'PAID' | 'OVERDUE' | 'CANCELLED'

export interface DashboardUpcomingJob {
  id: number
  title: string
  customerId: number
  customerName: string
  assigneeId: number | null
  assigneeName: string | null
  status: ServiceJobStatus
  priority: ServiceJobPriority
  scheduledAt: string | null
}

export interface DashboardInvoicePreview {
  id: number
  invoiceNumber: string
  customerId: number
  customerName: string
  status: InvoiceStatus
  issuedAt: string
  dueAt: string | null
  total: number
  paidAmount: number
  outstandingAmount: number
}

export interface DashboardSummary {
  totalCustomers: number
  totalAssets: number
  totalJobs: number
  jobsByStatus: Partial<Record<ServiceJobStatus, number>>
  jobsByPriority: Partial<Record<ServiceJobPriority, number>>
  urgentJobsCount: number
  totalInvoices: number
  invoicesByStatus: Partial<Record<InvoiceStatus, number>>
  overdueInvoicesCount: number
  invoicedTotal: number
  paidTotal: number
  outstandingTotal: number
  revenueThisMonth: number
  upcomingJobs: DashboardUpcomingJob[]
  unpaidInvoices: DashboardInvoicePreview[]
}

export interface BusinessProfile {
  businessName: string
  currency: string
}
