package edu.aseca.bags.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.aseca.bags.api.dto.CreateWalletRequest;
import edu.aseca.bags.persistence.SpringWalletJpaRepository;
import edu.aseca.bags.persistence.WalletEntity;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class WalletIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private SpringWalletJpaRepository walletRepo;

	@BeforeEach
	void setUp() {
		walletRepo.deleteAll();
	}

	@Test
	void shouldCreateWalletCorrectly_001() {
		CreateWalletRequest request = new CreateWalletRequest("e2e@correo.com", "claveSegura123");
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		var entity = new HttpEntity<>(request, headers);

		ResponseEntity<Void> response = restTemplate.postForEntity(getUrl(), entity, Void.class);

		assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
		assertTrue(walletRepo.existsByEmail("e2e@correo.com"));
	}

	@Test
	void itFailsWhileCreatingWalletWithExistingEmail_002() {
		walletRepo.save(new WalletEntity("ya@existe.com", "hash", BigDecimal.ZERO));

		CreateWalletRequest request = new CreateWalletRequest("ya@existe.com", "otraClave123");

		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		var entity = new HttpEntity<>(request, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(getUrl(), entity, String.class);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
	}

	@Test
	void failsWithShortPassword_003() {
		CreateWalletRequest request = new CreateWalletRequest("nuevo@correo.com", "123");

		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		var entity = new HttpEntity<>(request, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(getUrl(), entity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void failsWithInvalidEmail_004() {
		CreateWalletRequest request = new CreateWalletRequest("no-email", "claveSegura123");

		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		var entity = new HttpEntity<>(request, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(getUrl(), entity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void failsWithNotEmailInTheRequest_005() {
		String requestBody = """
				{
				 "password": "claveSegura123"
				}
				""";

		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		var entity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(getUrl(), entity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void failsWithNotPasswordInTheRequest_006() {
		String requestBody = """
				{
				"email": "fake@gmail.com"
				}
				""";

		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		var entity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(getUrl(), entity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	private static String getUrl() {
		return "/auth/register";
	}
}
