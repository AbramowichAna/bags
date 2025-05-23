package edu.aseca.bags.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class SecurityServiceTest {

	private SecurityService securityService;

	@BeforeEach
	void setUp() {
		securityService = new SecurityService();
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldReturnAuthenticatedEmail() {
		Authentication auth = new UsernamePasswordAuthenticationToken("user@example.com", null,
				Collections.emptyList());
		SecurityContextHolder.getContext().setAuthentication(auth);

		String email = securityService.getMail();

		assertEquals("user@example.com", email);
	}

	@Test
	void shouldThrowIfNotAuthenticated() {
		SecurityContextHolder.clearContext();

		assertThrows(BadCredentialsException.class, () -> securityService.getMail());
	}
}
