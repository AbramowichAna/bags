package edu.aseca.bags.domain.email;

public record RawPassword(String value) {

	public RawPassword {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("Password cannot be blank");
		}
		if (value.length() < 8) {
			throw new IllegalArgumentException("Password must be at least 8 characters");
		}
	}
}
