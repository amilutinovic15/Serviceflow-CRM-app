package com.serviceflow.job;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record ServiceJobResponse(
        Long id,
        Long customerId,
        String customerName,
        Long assigneeId,
        String assigneeName,
        String assigneeEmail,
        Long catalogItemId,
        String catalogItemName,
        String title,
        String description,
        ServiceJobStatus status,
        ServiceJobPriority priority,
        LocalDateTime scheduledAt,
        LocalDateTime completedAt,
        BigDecimal price,
        Instant createdAt,
        Instant updatedAt
) {
    static ServiceJobResponse from(ServiceJob job) {
        return new ServiceJobResponse(
                job.getId(),
                job.getCustomer().getId(),
                job.getCustomer().getFullName(),
                job.getAssignee() == null ? null : job.getAssignee().getId(),
                job.getAssignee() == null ? null : job.getAssignee().getFullName(),
                job.getAssignee() == null ? null : job.getAssignee().getEmail(),
                job.getCatalogItem() == null ? null : job.getCatalogItem().getId(),
                job.getCatalogItem() == null ? null : job.getCatalogItem().getName(),
                job.getTitle(),
                job.getDescription(),
                job.getStatus(),
                job.getPriority(),
                job.getScheduledAt(),
                job.getCompletedAt(),
                job.getPrice(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}
