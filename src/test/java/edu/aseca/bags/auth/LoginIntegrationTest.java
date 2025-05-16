package edu.aseca.bags.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.aseca.bags.domain.WalletRepository;
import edu.aseca.bags.testutil.TestWalletFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private WalletRepository walletRepository;

	@BeforeEach
	void setUp() {
		walletRepository.deleteAll();
		registerTestUser("cryptobro@gmail.com", "cryptopassword");
		registerTestUser("anothercryptobro@gmail.com", "visapassword");
	}

	private void registerTestUser(String email, String rawPassword) {
		walletRepository.save(TestWalletFactory.createWallet(email, rawPassword));
	}

	@ParameterizedTest
	@CsvSource({"cryptobro@gmail.com,cryptopassword,200", "anothercryptobro@gmail.com,visapassword,200",
			"cryptobro@gmail.com,wrongpassword,401", "notfound@example.com,anypassword,401", "'',somepassword,400",
			"user@example.com,'',400", "'','',400"})
	void login_shouldReturnExpectedStatus(String email, String password, int expectedStatus) {
		var request = new AuthRequest(email, password);
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		var entity = new HttpEntity<>(request, headers);
		var response = restTemplate.postForEntity("/auth/login", entity, AuthResponse.class);

		assertEquals(expectedStatus, response.getStatusCode().value());
		if (expectedStatus == 200) {
			assertNotNull(response.getBody().token());
		}
	}
}
