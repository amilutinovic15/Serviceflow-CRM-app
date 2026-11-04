package com.serviceflow.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceJobRequest(
        @NotNull Long customerId,
        Long assigneeId,
        Long catalogItemId,
        @NotBlank @Size(max = 140) String title,
        String description,
        ServiceJobStatus status,
        ServiceJobPriority priority,
        LocalDateTime scheduledAt,
        LocalDateTime completedAt,
        @PositiveOrZero BigDecimal price
) {
}
