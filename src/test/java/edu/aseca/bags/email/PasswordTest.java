package edu.aseca.bags.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PasswordTest {

	@Test
	public void shouldCreatePassword_001() {
		assertDoesNotThrow(() -> new Password("password 12345678"));
	}

	@Test
	public void shortPasswordShouldThrowException_002() {
		assertThrows(IllegalArgumentException.class, () -> new Password("abc123"));
	}
}