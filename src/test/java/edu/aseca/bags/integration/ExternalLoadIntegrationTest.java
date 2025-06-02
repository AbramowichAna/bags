package edu.aseca.bags.integration;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.api.dto.ExternalLoadRequest;
import edu.aseca.bags.api.dto.ExternalLoadResponse;
import edu.aseca.bags.persistence.SpringExternalLoadJpaRepository;
import edu.aseca.bags.persistence.SpringWalletJpaRepository;
import edu.aseca.bags.persistence.WalletEntity;
import java.math.BigDecimal;
import java.util.UUID;
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
	private SpringExternalLoadJpaRepository externalLoadJpaRepository;

	@BeforeEach
	void setUp() {
		externalLoadJpaRepository.deleteAll();
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

	@Test
	void shouldLoadMoneySuccessfully_001() {
		String email = "wallet1@gmail.com";
		createWallet(email, 0);

		ExternalLoadRequest request = new ExternalLoadRequest(email, BigDecimal.valueOf(150.0), "BANK_TRANSFER",
				UUID.randomUUID().toString());

		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		ResponseEntity<ExternalLoadResponse> response = restTemplate.postForEntity("/external-load", entity,
				ExternalLoadResponse.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(email, response.getBody().walletEmail());
		assertEquals(150.0, response.getBody().amount());
		assertEquals("SUCCESS", response.getBody().status());
	}

	@Test
	void shouldFailWithNonExistentWallet_002() {
		ExternalLoadRequest request = new ExternalLoadRequest("notfound@gmail.com", BigDecimal.valueOf(100.0),
				"BANK_TRANSFER", UUID.randomUUID().toString());

		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void shouldFailWithNegativeAmount_003() {
		String email = "wallet2@gmail.com";
		createWallet(email, 0);

		ExternalLoadRequest request = new ExternalLoadRequest(email, BigDecimal.valueOf(-10.0), "BANK_TRANSFER",
				UUID.randomUUID().toString());

		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailWithNullAmount_004() {
		String email = "wallet3@gmail.com";
		createWallet(email, 0);

		ExternalLoadRequest request = new ExternalLoadRequest(email, null, "BANK_TRANSFER",
				UUID.randomUUID().toString());

		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailWithMissingOrInvalidToken_005() {
		String email = "wallet4@gmail.com";
		createWallet(email, 0);

		ExternalLoadRequest request = new ExternalLoadRequest(email, BigDecimal.valueOf(50.0), "BANK_TRANSFER",
				UUID.randomUUID().toString());

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
		ExternalLoadRequest request = new ExternalLoadRequest(null, BigDecimal.valueOf(100.0), "BANK_TRANSFER",
				UUID.randomUUID().toString());
		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailWithInvalidTransferType_007() {
		String email = "wallet5@gmail.com";
		createWallet(email, 0);
		ExternalLoadRequest request = new ExternalLoadRequest(email, BigDecimal.valueOf(50.0), "INVALID_TYPE",
				UUID.randomUUID().toString());
		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailWithDuplicateExternalReference_008() {
		String email = "wallet6@gmail.com";
		createWallet(email, 0);
		String reference = UUID.randomUUID().toString();
		ExternalLoadRequest request = new ExternalLoadRequest(email, BigDecimal.valueOf(100.0), "BANK_TRANSFER",
				reference);
		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headersWithToken());
		restTemplate.postForEntity("/external-load", entity, String.class);

		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailWithEmptyToken_009() {
		String email = "wallet7@gmail.com";
		createWallet(email, 0);
		ExternalLoadRequest request = new ExternalLoadRequest(email, BigDecimal.valueOf(100.0), "BANK_TRANSFER",
				UUID.randomUUID().toString());
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-API-TOKEN", "");
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ExternalLoadRequest> entity = new HttpEntity<>(request, headers);
		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", entity, String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

}