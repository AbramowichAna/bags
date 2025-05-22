package edu.aseca.bags.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.aseca.bags.application.WalletRepository;
import edu.aseca.bags.persistence.SpringWalletJpaRepository;
import edu.aseca.bags.persistence.WalletMapper;
import edu.aseca.bags.testutil.JwtTestUtil;
import edu.aseca.bags.testutil.TestWalletFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

@Import(TestControllerConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PublicRoutesTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private SpringWalletJpaRepository walletRepository;

	@Autowired
	private JwtTestUtil jwtTestUtil;

	private static final String EMAIL = "cryptobro@gmail.com";
	private static final String PASSWORD = "cryptopassword";
	private String jwtToken;

	@BeforeEach
	void setUp() {
		walletRepository.deleteAll();
		walletRepository.save(WalletMapper.toEntity(TestWalletFactory.createWallet(EMAIL, PASSWORD)));
		jwtToken = jwtTestUtil.generateValidToken(EMAIL, 3600);
	}

	@Test
	void whenAccessPrivateRouteWithoutToken_thenReturns401() {
		ResponseEntity<String> response = restTemplate.getForEntity("/test/ping", String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	void whenAccessPrivateRouteWithToken_thenReturns200() {
		HttpHeaders headers = buildHeadersWithToken(jwtToken);
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange("/test/ping", HttpMethod.GET, entity, String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void whenAccessPrivateRouteWithExpiredToken_thenReturns401() {
		String expiredToken = jwtTestUtil.generateExpiredToken(EMAIL);

		HttpHeaders headers = buildHeadersWithToken(expiredToken);
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange("/test/ping", HttpMethod.GET, entity, String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	private HttpHeaders buildHeadersWithToken(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		return headers;
	}
}
