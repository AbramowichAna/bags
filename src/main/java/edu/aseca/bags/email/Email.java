package edu.aseca.bags.email;

import java.util.regex.Pattern;

public record Email(String address) {
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	public Email {
		if (address == null || !EMAIL_PATTERN.matcher(address).matches()) {
			throw new IllegalArgumentException("Invalid email format: " + address);
		}
	}
}