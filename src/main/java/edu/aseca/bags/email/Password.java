package edu.aseca.bags.email;

public record Password(String password) {

	public Password {
		if (password.length() < 8) {
			throw new IllegalArgumentException("Password must be at least 8 characters");
		}
	}
}
