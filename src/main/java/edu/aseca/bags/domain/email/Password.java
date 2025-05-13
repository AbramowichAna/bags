package edu.aseca.bags.domain.email;

import jakarta.persistence.Embeddable;

@Embeddable
public record Password(String password) {

	public Password {
		if (password.length() < 8) {
			throw new IllegalArgumentException("Password must be at least 8 characters");
		}
	}
}
