package edu.aseca.bags.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
		@NotBlank(message = "Email must not be blank") @Email(message = "Email must be a valid email address") String email,

		@NotBlank(message = "Password must not be blank") String password) {
}
