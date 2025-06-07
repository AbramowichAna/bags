package edu.aseca.bags.integration;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.api.TransferController.TransferRequest;
import edu.aseca.bags.application.dto.MovementView;
import edu.aseca.bags.persistence.entity.WalletEntity;
import edu.aseca.bags.persistence.repository.SpringMovementJpaRepository;
import edu.aseca.bags.persistence.repository.SpringWalletJpaRepository;
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
public class TransferIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private SpringMovementJpaRepository springMovementJpaRepository;

	@Autowired
	private SpringWalletJpaRepository springWalletJpaRepository;

	@Autowired
	private JwtTestUtil jwtTestUtil;

	@BeforeEach
	void setUp() {
		springMovementJpaRepository.deleteAll();
		springWalletJpaRepository.deleteAll();
	}

	private String getUrl() {
		return "/transfer";
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

		createWallet("wallet1@gmail.com", 100);
		createWallet("wallet2@gmail.com", 50);

		ResponseEntity<Void> response = performTransfer("wallet1@gmail.com", "wallet2@gmail.com", 100.0);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, springMovementJpaRepository.findAll().size());
	}

	@Test
	void shouldFailTransferWithInsufficientFunds_002() {

		createWallet("wallet1@gmail.com", 100);
		createWallet("wallet2@gmail.com", 50);

		ResponseEntity<Void> response = performTransfer("wallet1@gmail.com", "wallet2@gmail.com", 200.0);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailTransferToNonExistentWallet_003() {

		createWallet("wallet1@gmail.com", 100);

		ResponseEntity<Void> response = performTransfer("wallet1@gmail.com", "wallet2@gmail.com", 200.0);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void shouldFailTransferToSameWallet_004() {

		createWallet("wallet1@gmail.com", 100);

		ResponseEntity<Void> response = performTransfer("wallet1@gmail.com", "wallet1@gmail.com", 200.0);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldGetTransferHistory_005() {

		createWallet("wallet1@gmail.com", 100);
		createWallet("wallet2@gmail.com", 50);
		performTransfer("wallet1@gmail.com", "wallet2@gmail.com", 100.0);

		HttpEntity<Void> request = new HttpEntity<>(authHeadersFor("wallet1@gmail.com"));
		ResponseEntity<Void> response = restTemplate.exchange(getUrl(), HttpMethod.GET, request, Void.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void shouldFailTransferWithZeroAmount_006() {

		createWallet("wallet1@gmail.com", 100);
		createWallet("wallet2@gmail.com", 50);

		ResponseEntity<Void> response = performTransfer("wallet1@gmail.com", "wallet2@gmail.com", 0.0);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailTransferWithNegativeAmount_007() {

		createWallet("wallet1@gmail.com", 100);
		createWallet("wallet2@gmail.com", 50);

		ResponseEntity<Void> response = performTransfer("wallet1@gmail.com", "wallet2@gmail.com", -10.0);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldFailTransferWithEmptyRequestBody_008() {

		createWallet("wallet1@gmail.com", 100);
		HttpHeaders headers = authHeadersFor("wallet1@gmail.com");

		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<Void> response = restTemplate.postForEntity(getUrl(), entity, Void.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldReturnOnlyTransfersOfAuthenticatedWallet_009() {
		createWallet("wallet1@gmail.com", 100);
		createWallet("wallet2@gmail.com", 50);
		performTransfer("wallet1@gmail.com", "wallet2@gmail.com", 100.0);

		HttpEntity<Void> request = new HttpEntity<>(authHeadersFor("wallet1@gmail.com"));
		ResponseEntity<PageResponse<MovementView>> response = restTemplate.exchange(getUrl(), HttpMethod.GET, request,
				new ParameterizedTypeReference<>() {
				});

		assertEquals(HttpStatus.OK, response.getStatusCode());
		PageResponse<MovementView> page = response.getBody();
		assertNotNull(page);
		assertEquals(1, page.content.length);

		var transfer = page.content[0];
		assertAll(() -> assertEquals("wallet1@gmail.com", transfer.fromParticipant().email()),
				() -> assertEquals("wallet2@gmail.com", transfer.toParticipant().email()),
				() -> assertEquals(100.0, transfer.amount().doubleValue()), () -> assertNotNull(transfer.timestamp()),
				() -> assertNotNull(transfer.id()));
	}

	@Test
	void shouldNotReturnTransfersOfOtherWallets_010() {

		createWallet("wallet1@gmail.com", 100);
		createWallet("wallet2@gmail.com", 100);
		createWallet("wallet3@gmail.com", 100);

		performTransfer("wallet1@gmail.com", "wallet2@gmail.com", 10.0);
		performTransfer("wallet3@gmail.com", "wallet2@gmail.com", 20.0);

		HttpEntity<Void> request = new HttpEntity<>(authHeadersFor("wallet1@gmail.com"));
		ResponseEntity<PageResponse<MovementView>> response = restTemplate.exchange(getUrl(), HttpMethod.GET, request,
				new ParameterizedTypeReference<>() {
				});

		assertEquals(HttpStatus.OK, response.getStatusCode());
		PageResponse<MovementView> page = response.getBody();
		assertNotNull(page);
		assertEquals(1, page.content.length);

		var transfer = page.content[0];
		assertAll(() -> assertEquals("wallet1@gmail.com", transfer.fromParticipant().email()),
				() -> assertEquals("wallet2@gmail.com", transfer.toParticipant().email()),
				() -> assertEquals(10.0, transfer.amount().doubleValue()));
	}

	@Test
	void shouldReturnEmptyPageWhenNoTransfersExist_011() {
		createWallet("wallet1@gmail.com", 100);

		var entity = new HttpEntity<>(authHeadersFor("wallet1@gmail.com"));
		ResponseEntity<PageResponse<MovementView>> response = restTemplate.exchange(getUrl() + "?page=0&size=10",
				HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
				});

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(0, response.getBody().content.length);
	}

	@Test
	void shouldReturnBadRequestForNegativePage_012() {
		createWallet("wallet1@gmail.com", 100);

		var entity = new HttpEntity<>(authHeadersFor("wallet1@gmail.com"));
		ResponseEntity<String> response = restTemplate.exchange(getUrl() + "?page=-1&size=10", HttpMethod.GET, entity,
				String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldReturnBadRequestForNegativeSize_013() {
		createWallet("wallet1@gmail.com", 100);

		var entity = new HttpEntity<>(authHeadersFor("wallet1@gmail.com"));
		ResponseEntity<String> response = restTemplate.exchange(getUrl() + "?page=0&size=-5", HttpMethod.GET, entity,
				String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldReturnBadRequestForZeroSize_014() {
		createWallet("wallet1@gmail.com", 100);

		var entity = new HttpEntity<>(authHeadersFor("wallet1@gmail.com"));
		ResponseEntity<String> response = restTemplate.exchange(getUrl() + "?page=0&size=0", HttpMethod.GET, entity,
				String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldReturnEmptyPageIfPageIsTooHigh_015() {
		createWallet("wallet1@gmail.com", 100);
		createWallet("wallet2@gmail.com", 50);
		performTransfer("wallet1@gmail.com", "wallet2@gmail.com", 10);

		var entity = new HttpEntity<>(authHeadersFor("wallet1@gmail.com"));
		ResponseEntity<PageResponse<MovementView>> response = restTemplate.exchange(getUrl() + "?page=100&size=10",
				HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
				});

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(0, response.getBody().content.length);
	}

	@Test
	void shouldReturnBadRequestForNonNumericPageAndSize_016() {
		createWallet("wallet1@gmail.com", 100);

		var entity = new HttpEntity<>(authHeadersFor("wallet1@gmail.com"));
		ResponseEntity<String> response = restTemplate.exchange(getUrl() + "?page=abc&size=xyz", HttpMethod.GET, entity,
				String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	private void createWallet(String email, double balance) {
		springWalletJpaRepository.save(new WalletEntity(email, "hash", BigDecimal.valueOf(balance)));
	}

	private ResponseEntity<Void> performTransfer(String fromEmail, String toEmail, double amount) {
		HttpHeaders headers = authHeadersFor(fromEmail);
		TransferRequest request = new TransferRequest(toEmail, amount);
		return restTemplate.postForEntity(getUrl(), new HttpEntity<>(request, headers), Void.class);
	}

	private HttpHeaders authHeadersFor(String email) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(jwtTestUtil.generateValidToken(email, 3600));
		return headers;
	}

}
