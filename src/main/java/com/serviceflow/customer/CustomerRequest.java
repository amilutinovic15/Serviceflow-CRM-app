package com.serviceflow.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank @Size(max = 120) String fullName,
        @Email @Size(max = 160) String email,
        @Size(max = 40) String phone,
        @Size(max = 120) String companyName,
        @Size(max = 255) String address,
        String notes
) {
}
