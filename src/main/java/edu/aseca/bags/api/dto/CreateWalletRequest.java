package edu.aseca.bags.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWalletRequest(
		@NotBlank(message = "Email must not be blank") @Email(message = "Email must be a valid email address") String email,

		@Size(min = 8, message = "Password must be at least 8 characters long") String password) {
	public CreateWalletRequest {
		email = email != null ? email.trim() : null;
	}
}
