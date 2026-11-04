package com.serviceflow.customer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record CustomerTimelineItemResponse(
        Instant occurredAt,
        String type,
        String title,
        String description,
        Long referenceId,
        BigDecimal amount,
        Map<String, Object> metadata
) {
}
