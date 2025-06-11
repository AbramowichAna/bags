package edu.aseca.bags.integration;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.api.dto.DebInRequest;
import edu.aseca.bags.persistence.entity.WalletEntity;
import edu.aseca.bags.persistence.repository.SpringMovementJpaRepository;
import edu.aseca.bags.persistence.repository.SpringWalletJpaRepository;
import edu.aseca.bags.testutil.Defaults;
import edu.aseca.bags.testutil.JwtTestUtil;
import java.math.BigDecimal;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("integration")
@Import(TestcontainersConfiguration.class)
public class DebInIntegrationTest {

	private static TestRestTemplate restTemplate;

	@Autowired
	private SpringWalletJpaRepository walletJpaRepository;

	@Autowired
	private SpringMovementJpaRepository movementJpaRepository;

	@Autowired
	private JwtTestUtil jwtTestUtil;

	private static final int PORT = 60000;
	private static final String HOST = "http://localhost";

	private String getBaseUrl() {
		return HOST + ":" + PORT;
	}

	@BeforeAll
	static void init() {
		restTemplate = new TestRestTemplate();
	}

	@BeforeEach
	void setUp() {
		movementJpaRepository.deleteAll();
		walletJpaRepository.deleteAll();
	}

	private void createWallet(String email) {
		walletJpaRepository.save(new WalletEntity(email, "hash", BigDecimal.valueOf(0)));
	}

	private HttpHeaders authHeadersFor(String email) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(jwtTestUtil.generateValidToken(email, 3600));
		return headers;
	}

	@Test
	void shouldSucceedInRequestingDebin() {
		String email = Defaults.getDefaultEmail().address();
		createWallet(email);

		HttpHeaders headers = authHeadersFor(email);

		DebInRequest request = new DebInRequest("Bank", "BANK", "alice@example.com", 1);

		HttpEntity<DebInRequest> entity = new HttpEntity<>(request, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(getDebinUrl(), entity, String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@NotNull
	private String getDebinUrl() {
		return getBaseUrl() + "/debin";
	}

	@Test
	void shouldFailWithNonExistentWallet_001() {
		String email = "nonexistent@gmail.com";

		DebInRequest request = new DebInRequest("Bank", "BANK", "external@bank.com", 100.0);

		HttpHeaders headers = authHeadersFor(email);

		HttpEntity<DebInRequest> entity = new HttpEntity<>(request, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(getDebinUrl(), entity, String.class);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	void shouldFailWithNegativeAmount_002() {
		String email = "wallet2@gmail.com";
		createWallet(email);

		DebInRequest request = new DebInRequest("Bank", "BANK", "external@bank.com", -10.0);

		HttpHeaders headers = authHeadersFor(email);

		HttpEntity<DebInRequest> entity = new HttpEntity<>(request, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(getDebinUrl(), entity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailWithInvalidService_003() {
		String email = "wallet3@gmail.com";
		createWallet(email);

		DebInRequest request = new DebInRequest("UnknownService", "BANK", "external@bank.com", 100.0);

		HttpHeaders headers = authHeadersFor(email);

		HttpEntity<DebInRequest> entity = new HttpEntity<>(request, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(getDebinUrl(), entity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().contains("Unsupported external service"));
	}
}