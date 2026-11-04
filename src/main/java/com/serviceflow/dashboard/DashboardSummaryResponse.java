package com.serviceflow.dashboard;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardSummaryResponse(
        long totalCustomers,
        long totalAssets,
        long totalJobs,
        Map<String, Long> jobsByStatus,
        Map<String, Long> jobsByPriority,
        long urgentJobsCount,
        long totalInvoices,
        Map<String, Long> invoicesByStatus,
        long overdueInvoicesCount,
        BigDecimal invoicedTotal,
        BigDecimal paidTotal,
        BigDecimal outstandingTotal,
        BigDecimal revenueThisMonth,
        List<DashboardUpcomingJobResponse> upcomingJobs,
        List<DashboardInvoicePreviewResponse> unpaidInvoices
) {
}
