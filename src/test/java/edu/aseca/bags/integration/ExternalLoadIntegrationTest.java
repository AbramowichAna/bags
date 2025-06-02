package edu.aseca.bags.integration;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.api.dto.ExternalLoadRequest;
import edu.aseca.bags.api.dto.ExternalLoadResponse;
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

	@BeforeEach
	void setUp() {
		walletJpaRepository.deleteAll();
	}

	private void createWallet(String email, double balance) {
		walletJpaRepository.save(new WalletEntity(email, "hash", BigDecimal.valueOf(balance)));
	}

	@Test
	void shouldLoadMoneySuccessfully_001() {
		String email = "wallet1@gmail.com";
		createWallet(email, 0);

		ExternalLoadRequest request = new ExternalLoadRequest(email, BigDecimal.valueOf(150.0), "BANK_TRANSFER",
				UUID.randomUUID().toString());

		ResponseEntity<ExternalLoadResponse> response = restTemplate.postForEntity("/external-load", request,
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

		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", request, String.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void shouldFailWithNegativeAmount_003() {
		String email = "wallet2@gmail.com";
		createWallet(email, 0);

		ExternalLoadRequest request = new ExternalLoadRequest(email, BigDecimal.valueOf(-10.0), "BANK_TRANSFER",
				UUID.randomUUID().toString());

		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", request, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailWithNullAmount_004() {
		String email = "wallet3@gmail.com";
		createWallet(email, 0);

		ExternalLoadRequest request = new ExternalLoadRequest(email, null, "BANK_TRANSFER",
				UUID.randomUUID().toString());

		ResponseEntity<String> response = restTemplate.postForEntity("/external-load", request, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
}