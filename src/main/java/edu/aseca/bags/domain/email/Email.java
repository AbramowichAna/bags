package edu.aseca.bags.domain.email;

import java.util.regex.Pattern;

public record Email(String address) {
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^(?!.*\\.\\.)[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
			Pattern.CASE_INSENSITIVE);

	public Email {
		if (address == null || !EMAIL_PATTERN.matcher(address).matches()) {
			throw new IllegalArgumentException("Invalid email format: " + address);
		}
	}
}