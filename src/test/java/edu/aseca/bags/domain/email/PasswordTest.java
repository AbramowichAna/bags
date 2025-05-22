package edu.aseca.bags.domain.email;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordTest {

	@ParameterizedTest
	@ValueSource(strings = {"password12345678", "ThisIsASecurePassword123"})
	void shouldCreatePassword(String validPassword) {
		assertDoesNotThrow(() -> new Password(validPassword));
	}

	@ParameterizedTest
	@ValueSource(strings = {""})
	void shortPasswordShouldThrowException(String invalidPassword) {
		assertThrows(IllegalArgumentException.class, () -> new Password(invalidPassword));
	}
}