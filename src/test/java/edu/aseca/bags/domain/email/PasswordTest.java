package edu.aseca.bags.domain.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PasswordTest {

	@ParameterizedTest
	@ValueSource(strings = {"password12345678", "ThisIsASecurePassword123", "long enough password!", "abcd1234efgh5678",
			"p@ssw0rd_with_symbols"})
	void shouldCreatePassword(String validPassword) {
		assertDoesNotThrow(() -> new Password(validPassword));
	}

	@ParameterizedTest
	@ValueSource(strings = {"abc123", "1234567", "pass", "", "       ", "short1!"})
	void shortPasswordShouldThrowException(String invalidPassword) {
		assertThrows(IllegalArgumentException.class, () -> new Password(invalidPassword));
	}
}