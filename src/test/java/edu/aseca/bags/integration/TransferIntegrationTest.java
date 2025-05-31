package edu.aseca.bags.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.aseca.bags.api.TransferController;
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

	public static class PageResponse<T> {
		public T[] content;
		public int number;
		public int size;
		public int totalPages;
		public long totalElements;
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

	@Test
	void shouldFailTransferWithZeroAmount_006() {
		springWalletJpaRepository.save(new WalletEntity("wallet1@gmail.com", "hash", BigDecimal.valueOf(100)));
		springWalletJpaRepository.save(new WalletEntity("wallet2@gmail.com", "hash", BigDecimal.valueOf(50)));

		TransferRequest request = new TransferRequest("wallet2@gmail.com", 0.0);
		var entity = new HttpEntity<>(request, jsonHeadersWithAuth());

		ResponseEntity<Void> response = restTemplate.postForEntity(getUrl(), entity, Void.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailTransferWithNegativeAmount_007() {
		springWalletJpaRepository.save(new WalletEntity("wallet1@gmail.com", "hash", BigDecimal.valueOf(100)));
		springWalletJpaRepository.save(new WalletEntity("wallet2@gmail.com", "hash", BigDecimal.valueOf(50)));

		TransferRequest request = new TransferRequest("wallet2@gmail.com", -10.0);
		var entity = new HttpEntity<>(request, jsonHeadersWithAuth());

		ResponseEntity<Void> response = restTemplate.postForEntity(getUrl(), entity, Void.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailTransferWithEmptyRequestBody_008() {
		springWalletJpaRepository.save(new WalletEntity("wallet1@gmail.com", "hash", BigDecimal.valueOf(100)));
		HttpHeaders headers = jsonHeadersWithAuth();

		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<Void> response = restTemplate.postForEntity(getUrl(), entity, Void.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldReturnOnlyTransfersOfAuthenticatedWallet_009() {
		springWalletJpaRepository.save(new WalletEntity("wallet1@gmail.com", "hash", BigDecimal.valueOf(100)));
		springWalletJpaRepository.save(new WalletEntity("wallet2@gmail.com", "hash", BigDecimal.valueOf(50)));

		TransferRequest request = new TransferRequest("wallet2@gmail.com", 100.0);
		var postEntity = new HttpEntity<>(request, jsonHeadersWithAuth());
		restTemplate.postForEntity(getUrl(), postEntity, Void.class);

		var getEntity = new HttpEntity<>(jsonHeadersWithAuth());
		ResponseEntity<PageResponse<TransferController.TransferResponse>> response = restTemplate.exchange(getUrl(),
				HttpMethod.GET, getEntity, new org.springframework.core.ParameterizedTypeReference<>() {
				});

		assertEquals(HttpStatus.OK, response.getStatusCode());
		PageResponse<TransferController.TransferResponse> page = response.getBody();
		assertEquals(1, page.content.length);
		var transfer = page.content[0];
		assertEquals("wallet1@gmail.com", transfer.fromEmail());
		assertEquals("wallet2@gmail.com", transfer.toEmail());
		assertEquals(100.0, transfer.amount());
		assertNotNull(transfer.timestamp());
		assertNotNull(transfer.transferNumber());
	}

	@Test
	void shouldNotReturnTransfersOfOtherWallets_011() {
		springWalletJpaRepository.save(new WalletEntity("wallet1@gmail.com", "hash", BigDecimal.valueOf(100)));
		springWalletJpaRepository.save(new WalletEntity("wallet2@gmail.com", "hash", BigDecimal.valueOf(100)));
		springWalletJpaRepository.save(new WalletEntity("wallet3@gmail.com", "hash", BigDecimal.valueOf(100)));

		TransferRequest req1 = new TransferRequest("wallet2@gmail.com", 10.0);
		restTemplate.postForEntity(getUrl(), new HttpEntity<>(req1, jsonHeadersWithAuth()), Void.class);

		var headersWallet3 = new HttpHeaders();
		headersWallet3.setContentType(MediaType.APPLICATION_JSON);
		headersWallet3.setBearerAuth(jwtTestUtil.generateValidToken("wallet3@gmail.com", 3600));
		TransferRequest req2 = new TransferRequest("wallet2@gmail.com", 20.0);
		restTemplate.postForEntity(getUrl(), new HttpEntity<>(req2, headersWallet3), Void.class);

		var getEntity = new HttpEntity<>(jsonHeadersWithAuth());
		ResponseEntity<PageResponse<TransferController.TransferResponse>> response = restTemplate.exchange(getUrl(),
				HttpMethod.GET, getEntity, new org.springframework.core.ParameterizedTypeReference<>() {
				});

		assertEquals(HttpStatus.OK, response.getStatusCode());
		PageResponse<TransferController.TransferResponse> page = response.getBody();
		assertNotNull(page);
		assertEquals(1, page.content.length);
		var transfer = page.content[0];
		assertEquals("wallet1@gmail.com", transfer.fromEmail());
		assertEquals("wallet2@gmail.com", transfer.toEmail());
		assertEquals(10.0, transfer.amount());
	}

}
