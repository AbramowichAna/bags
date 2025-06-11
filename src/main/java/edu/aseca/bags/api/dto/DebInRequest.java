package edu.aseca.bags.api.dto;

import jakarta.validation.constraints.*;

public record DebInRequest(@NotBlank String externalServiceName, @NotBlank String serviceType,
		@Email String externalEmail, @Positive double amount) {
}