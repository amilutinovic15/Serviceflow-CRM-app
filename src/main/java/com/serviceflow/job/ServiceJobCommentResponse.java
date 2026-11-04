package com.serviceflow.job;

import java.time.Instant;

public record ServiceJobCommentResponse(
        Long id,
        Long serviceJobId,
        String authorEmail,
        String body,
        Instant createdAt
) {
    static ServiceJobCommentResponse from(ServiceJobComment comment) {
        return new ServiceJobCommentResponse(
                comment.getId(),
                comment.getServiceJob().getId(),
                comment.getAuthorEmail(),
                comment.getBody(),
                comment.getCreatedAt()
        );
    }
}
