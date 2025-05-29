package edu.aseca.bags.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.aseca.bags.api.TransferController.TransferRequest;
import edu.aseca.bags.persistence.SpringTransferJpaRepository;
import edu.aseca.bags.persistence.SpringWalletJpaRepository;
import edu.aseca.bags.persistence.WalletEntity;
import edu.aseca.bags.testutil.JwtTestUtil;
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
public class TransferIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private SpringTransferJpaRepository springTransferJpaRepository;

	@Autowired
	private SpringWalletJpaRepository springWalletJpaRepository;

	@Autowired
	private JwtTestUtil jwtTestUtil;

	@BeforeEach
	void setUp() {
		springTransferJpaRepository.deleteAll();
		springWalletJpaRepository.deleteAll();
	}

	private String getUrl() {
		return "/transfer";
	}

	private HttpHeaders jsonHeadersWithAuth() {
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(jwtTestUtil.generateValidToken("wallet1@gmail.com", 3600));
		return headers;
	}

	@Test
	void shouldTransferMoneyAndIsSuccessful_001() {

		springWalletJpaRepository.save(new WalletEntity("wallet1@gmail.com", "hash", BigDecimal.valueOf(100)));
		springWalletJpaRepository.save(new WalletEntity("wallet2@gmail.com", "hash", BigDecimal.valueOf(50)));

		TransferRequest request = new TransferRequest("wallet2@gmail.com", 100.0);
		var entity = new HttpEntity<>(request, jsonHeadersWithAuth());

		ResponseEntity<Void> response = restTemplate.postForEntity(getUrl(), entity, Void.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		var transfers = springTransferJpaRepository.findAll();
		assertEquals(1, transfers.size());
	}

	@Test
	void shouldFailTransferWithInsufficientFunds_002() {

		springWalletJpaRepository.save(new WalletEntity("wallet1@gmail.com", "hash", BigDecimal.valueOf(100)));
		springWalletJpaRepository.save(new WalletEntity("wallet2@gmail.com", "hash", BigDecimal.valueOf(50)));

		TransferRequest request = new TransferRequest("wallet2@gmail.com", 200.0);
		var entity = new HttpEntity<>(request, jsonHeadersWithAuth());

		ResponseEntity<Void> response = restTemplate.postForEntity(getUrl(), entity, Void.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailTransferToNonExistentWallet_003() {

		springWalletJpaRepository.save(new WalletEntity("wallet1@gmail.com", "hash", BigDecimal.valueOf(100)));

		TransferRequest request = new TransferRequest("wallet2@gmail.com", 200.0);
		var entity = new HttpEntity<>(request, jsonHeadersWithAuth());

		ResponseEntity<Void> response = restTemplate.postForEntity(getUrl(), entity, Void.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

	}

	@Test
	void shouldFrailTransferToSameWallet_004() {

		springWalletJpaRepository.save(new WalletEntity("wallet1@gmail.com", "hash", BigDecimal.valueOf(100)));

		TransferRequest request = new TransferRequest("wallet1@gmail.com", 200.0);
		var entity = new HttpEntity<>(request, jsonHeadersWithAuth());

		ResponseEntity<Void> response = restTemplate.postForEntity(getUrl(), entity, Void.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

	}

	@Test
	void shouldGetTransferHistory_005() {

		springWalletJpaRepository.save(new WalletEntity("wallet1@gmail.com", "hash", BigDecimal.valueOf(100)));
		springWalletJpaRepository.save(new WalletEntity("wallet2@gmail.com", "hash", BigDecimal.valueOf(50)));

		TransferRequest request = new TransferRequest("wallet2@gmail.com", 100.0);
		var postEntity = new HttpEntity<>(request, jsonHeadersWithAuth());

		restTemplate.postForEntity(getUrl(), postEntity, Void.class);

		var getEntity = new HttpEntity<>(jsonHeadersWithAuth());
		ResponseEntity<Void> response = restTemplate.exchange(getUrl(), HttpMethod.GET, getEntity, Void.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

}
