package com.serviceflow.customer;

import java.util.List;

public record CustomerTimelineResponse(
        Long customerId,
        String customerName,
        List<CustomerTimelineItemResponse> items
) {
}
