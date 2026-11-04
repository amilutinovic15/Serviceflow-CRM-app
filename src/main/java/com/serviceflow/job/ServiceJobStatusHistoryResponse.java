package com.serviceflow.job;

import java.time.Instant;

public record ServiceJobStatusHistoryResponse(
        Long id,
        Long serviceJobId,
        ServiceJobStatus previousStatus,
        ServiceJobStatus newStatus,
        String changedByEmail,
        String note,
        Instant changedAt
) {
    static ServiceJobStatusHistoryResponse from(ServiceJobStatusHistory history) {
        return new ServiceJobStatusHistoryResponse(
                history.getId(),
                history.getServiceJob().getId(),
                history.getPreviousStatus(),
                history.getNewStatus(),
                history.getChangedByEmail(),
                history.getNote(),
                history.getChangedAt()
        );
    }
}
