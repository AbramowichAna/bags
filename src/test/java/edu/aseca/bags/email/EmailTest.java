package edu.aseca.bags.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EmailTest {

	@Test
	void shouldCreateEmailWithValidFormat_001() {
		String validEmail = "user@example.com";
		assertDoesNotThrow(() -> new Email(validEmail));
		Email email = new Email(validEmail);
		assertEquals(validEmail, email.address());
	}

	@Test
	void shouldNotCreateEmailWithInvalidFormat_004() {
		String invalidEmail = "invalid-email";
		assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
	}

}