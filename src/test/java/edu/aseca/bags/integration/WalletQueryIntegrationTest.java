package edu.aseca.bags.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.aseca.bags.application.dto.WalletInfo;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Import(TestcontainersConfiguration.class)
public class WalletQueryIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private SpringWalletJpaRepository walletRepo;

	@Autowired
	private JwtTestUtil jwtTestUtil;

	private final String email = "gregorsamsa@gmail.com";

	@BeforeEach
	void setUp() {
		walletRepo.deleteAll();
		walletRepo.save(WalletMapper.toEntity(TestWalletFactory.createWallet(email, "password", 100)));
	}

	@Test
	void shouldReturnWalletInfo_whenWalletExistsAndUserIsOwner_001() {
		var jwtToken = jwtTestUtil.generateValidToken(email, 3600);

		HttpEntity<Void> request = new HttpEntity<>(buildHeadersWithToken(jwtToken));

		ResponseEntity<WalletInfo> response = restTemplate.exchange("/wallet", HttpMethod.GET, request,
				WalletInfo.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(100, response.getBody().balance().amount());
	}

	@Test
	void shouldReturnUnauthorized_whenNoAuthHeaderProvided_002() {
		ResponseEntity<String> response = restTemplate.getForEntity("/wallet", String.class);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	private HttpHeaders buildHeadersWithToken(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		return headers;
	}
}
