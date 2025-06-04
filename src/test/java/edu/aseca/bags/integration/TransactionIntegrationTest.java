package edu.aseca.bags.integration;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.application.dto.TransactionView;
import edu.aseca.bags.persistence.*;
import edu.aseca.bags.testutil.JwtTestUtil;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Import(TestcontainersConfiguration.class)
public class TransactionIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private SpringWalletJpaRepository walletRepo;

	@Autowired
	private SpringTransferJpaRepository transferRepo;

	@Autowired
	private SpringExternalLoadJpaRepository externalLoadRepo;

	@Autowired
	private JwtTestUtil jwtTestUtil;

	@BeforeEach
	void setUp() {
		transferRepo.deleteAll();
		externalLoadRepo.deleteAll();
		walletRepo.deleteAll();
	}

	private String getUrl() {
		return "/transaction";
	}

	public static class PageResponse<T> {
		public T[] content;
		public int number;
		public int size;
		public int totalPages;
		public long totalElements;
	}

	@Test
	void shouldReturnTransactionsForWallet_001() {
		createWallet("wallet1@gmail.com", 100);
		createWallet("wallet2@gmail.com", 50);

		transferRepo.save(new TransferEntity(java.util.UUID.randomUUID(),
				walletRepo.findByEmail("wallet1@gmail.com").orElseThrow(),
				walletRepo.findByEmail("wallet2@gmail.com").orElseThrow(), java.math.BigDecimal.valueOf(10.0),
				java.time.Instant.now()));

		HttpHeaders headers = authHeadersFor();
		HttpEntity<Void> request = new HttpEntity<>(headers);

		ResponseEntity<PageResponse<TransactionView>> response = restTemplate.exchange(getUrl() + "?page=0&size=10",
				HttpMethod.GET, request, new ParameterizedTypeReference<>() {
				});

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().content.length > 0);
	}

	@Test
	void shouldReturnEmptyPageWhenNoTransactions_002() {
		createWallet("wallet1@gmail.com", 100);

		HttpHeaders headers = authHeadersFor();
		HttpEntity<Void> request = new HttpEntity<>(headers);

		ResponseEntity<PageResponse<TransactionView>> response = restTemplate.exchange(getUrl() + "?page=0&size=10",
				HttpMethod.GET, request, new ParameterizedTypeReference<>() {
				});

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(0, response.getBody().content.length);
	}

	@Test
	void shouldReturnBadRequestForInvalidPageOrSize_003() {
		createWallet("wallet1@gmail.com", 100);

		HttpHeaders headers = authHeadersFor();
		HttpEntity<Void> request = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(getUrl() + "?page=-1&size=10", HttpMethod.GET, request,
				String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	private void createWallet(String email, double balance) {
		walletRepo.save(new WalletEntity(email, "hash", BigDecimal.valueOf(balance)));
	}

	private HttpHeaders authHeadersFor() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(jwtTestUtil.generateValidToken("wallet1@gmail.com", 3600));
		return headers;
	}
}