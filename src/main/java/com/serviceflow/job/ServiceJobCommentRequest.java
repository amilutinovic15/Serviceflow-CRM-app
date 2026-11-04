package com.serviceflow.job;

import jakarta.validation.constraints.NotBlank;

public record ServiceJobCommentRequest(
        @NotBlank String body
) {
}
