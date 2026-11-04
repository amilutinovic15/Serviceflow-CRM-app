package com.serviceflow.dashboard;

import com.serviceflow.job.ServiceJobPriority;
import com.serviceflow.job.ServiceJobStatus;
import java.time.LocalDateTime;

public record DashboardUpcomingJobResponse(
        Long id,
        String title,
        Long customerId,
        String customerName,
        Long assigneeId,
        String assigneeName,
        ServiceJobStatus status,
        ServiceJobPriority priority,
        LocalDateTime scheduledAt
) {
}
