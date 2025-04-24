package edu.aseca.bags.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {
	@ParameterizedTest
	@ValueSource(strings = {"user@example.com", "john.doe@domain.co", "hello.world+test@sub.mail.com",
			"test_email-123@domain.net"})
	void shouldCreateEmailWithValidFormat(String validEmail) {
		assertDoesNotThrow(() -> new Email(validEmail));
		Email email = new Email(validEmail);
		assertEquals(validEmail, email.address());
	}

	@ParameterizedTest
	@ValueSource(strings = {"invalid-email", "user@.com", "@domain.com", "user@domain", "user@domain..com",
			"user@domain,com", "user@domain com", "user@", "plainaddress", "user@.invalid"})
	void shouldNotCreateEmailWithInvalidFormat(String invalidEmail) {
		assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
	}

}