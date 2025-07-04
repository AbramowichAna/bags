package edu.aseca.bags.application.dto;

import edu.aseca.bags.domain.participant.ServiceType;
import jakarta.validation.constraints.*;

public record DebInRequest(@NotBlank String externalServiceName, @NotNull ServiceType serviceType,
		@Email String externalEmail, @Positive double amount) {
}