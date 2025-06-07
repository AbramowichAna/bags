package edu.aseca.bags.integration;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.api.dto.ExternalLoadRequest;
import edu.aseca.bags.application.dto.MovementView;
import edu.aseca.bags.persistence.entity.WalletEntity;
import edu.aseca.bags.persistence.repository.SpringMovementJpaRepository;
import edu.aseca.bags.persistence.repository.SpringWalletJpaRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Import(TestcontainersConfiguration.class)
public class ExternalLoadIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private SpringWalletJpaRepository walletJpaRepository;

	@Autowired
	private SpringMovementJpaRepository movementJpaRepository;

	@BeforeEach
	void setUp() {
		movementJpaRepository.deleteAll();
		walletJpaRepository.deleteAll();
	}

	private void createWallet(String email, double balance) {
		walletJpaRepository.save(new WalletEntity(email, "hash", BigDecimal.valueOf(balance)));
	}

	private static final String API_TOKEN = "test_token";

	private HttpHeaders headersWithToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-API-TOKEN", API_TOKEN);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	private ExternalLoadRequest makeRequest(String walletEmail, BigDecimal amount, String serviceName,
			String serviceType, String serviceEmail) {
		return new ExternalLoadRequest(walletEmail, amount, serviceName, serviceType, serviceEmail);
	}

	@Test
	void shouldLoadMoneySuccessfully_001() {
		String email = "wallet1@gmail.com";
		createWallet(email, 0);

		ExternalLoadRequest request = makeRequest(email, BigDecimal.valueOf(150.0), "BANK_TRANSFER", "BANK",
				"bank@bank.com");

		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		ResponseEntity<MovementView> response = restTemplate.postForEntity("/external-load", entity,
				MovementView.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(email, response.getBody().toParticipant().email());
		assertEquals(150.0, response.getBody().amount().doubleValue());
		assertEquals("BANK_TRANSFER", response.getBody().fromParticipant().serviceName());
		assertEquals("BANK", response.getBody().fromParticipant().serviceType());
		assertEquals("bank@bank.com", response.getBody().fromParticipant().email());

	}

	@Test
	void shouldFailWithNonExistentWallet_002() {
		ExternalLoadRequest request = makeRequest("notfound@gmail.com", BigDecimal.valueOf(100.0), "BANK_TRANSFER",
				"BANK", "bank@bank.com");

		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void shouldFailWithNegativeAmount_003() {
		String email = "wallet2@gmail.com";
		createWallet(email, 0);

		ExternalLoadRequest request = makeRequest(email, BigDecimal.valueOf(-10.0), "BANK_TRANSFER", "BANK",
				"bank@bank.com");

		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailWithNullAmount_004() {
		String email = "wallet3@gmail.com";
		createWallet(email, 0);

		ExternalLoadRequest request = makeRequest(email, null, "BANK_TRANSFER", "BANK", "bank@bank.com");

		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailWithMissingOrInvalidToken_005() {
		String email = "wallet4@gmail.com";
		createWallet(email, 0);

		ExternalLoadRequest request = makeRequest(email, BigDecimal.valueOf(50.0), "BANK_TRANSFER", "BANK",
				"bank@bank.com");

		HttpEntity<ExternalLoadRequest> entityNoToken = new HttpEntity<>(request);
		ResponseEntity<String> responseNoToken = restTemplate.postForEntity("/external-load", entityNoToken,
				String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, responseNoToken.getStatusCode());

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-API-TOKEN", "invalid_token");
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ExternalLoadRequest> entityInvalidToken = new HttpEntity<>(request, headers);
		ResponseEntity<String> responseInvalidToken = restTemplate.postForEntity("/external-load", entityInvalidToken,
				String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, responseInvalidToken.getStatusCode());
	}

	@Test
	void shouldFailWithMissingEmail_006() {
		ExternalLoadRequest request = makeRequest(null, BigDecimal.valueOf(100.0), "BANK_TRANSFER", "BANK",
				"bank@bank.com");
		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailWithInvalidTransferType_007() {
		String email = "wallet5@gmail.com";
		createWallet(email, 0);
		ExternalLoadRequest request = makeRequest(email, BigDecimal.valueOf(50.0), "BANK_TRANSFER", "INVALID_TYPE",
				"bank@bank.com");
		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailWithEmptyToken_008() {
		String email = "wallet7@gmail.com";
		createWallet(email, 0);
		ExternalLoadRequest request = makeRequest(email, BigDecimal.valueOf(100.0), "BANK_TRANSFER", "BANK",
				"bank@bank.com");
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-API-TOKEN", "");
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headers);
		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
}