package edu.aseca.bags.domain.email;

public record Password(String hash) {

	public Password {
		if (hash == null || hash.isBlank()) {
			throw new IllegalArgumentException("Password hash cannot be null or blank");
		}
	}
}