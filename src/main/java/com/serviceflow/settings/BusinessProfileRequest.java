package com.serviceflow.settings;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BusinessProfileRequest(
        @NotBlank @Size(max = 160) String businessName,
        @Size(max = 160) String legalName,
        @Size(max = 80) String taxNumber,
        @Size(max = 80) String registrationNumber,
        @Email @Size(max = 160) String email,
        @Size(max = 40) String phone,
        @Size(max = 160) String website,
        @Size(max = 255) String address,
        @Size(max = 120) String city,
        @Size(max = 120) String country,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency,
        String invoiceFooter
) {
}
