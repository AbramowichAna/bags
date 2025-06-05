package edu.aseca.bags.auth;

import static edu.aseca.bags.exception.GlobalExceptionHandler.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.aseca.bags.api.dto.AuthRequest;
import edu.aseca.bags.api.dto.AuthResponse;
import edu.aseca.bags.api.dto.CreateWalletRequest;
import edu.aseca.bags.integration.TestcontainersConfiguration;
import edu.aseca.bags.persistence.SpringWalletJpaRepository;
import edu.aseca.bags.persistence.WalletMapper;
import edu.aseca.bags.testutil.TestWalletFactory;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Import(TestcontainersConfiguration.class)
public class LoginIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private SpringWalletJpaRepository walletRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		walletRepository.deleteAll();
		registerTestUser("cryptobro@gmail.com", "cryptopassword");
		registerTestUser("anothercryptobro@gmail.com", "visapassword");
	}

	private void registerTestUser(String email, String rawPassword) {
		walletRepository.save(WalletMapper.toEntity(TestWalletFactory.createWallet(email, rawPassword)));
	}

	@ParameterizedTest
	@CsvSource({"cryptobro@gmail.com,cryptopassword", "anothercryptobro@gmail.com,visapassword"})
	void login_shouldSucceed_withValidCredentials(String email, String password) throws Exception {
		ResponseEntity<String> response = performLoginRequest(email, password);

		assertEquals(200, response.getStatusCode().value());

		AuthResponse body = objectMapper.readValue(response.getBody(), AuthResponse.class);
		assertNotNull(body.token(), "Expected token on successful login");
	}

	@ParameterizedTest
	@CsvSource({"newuser@gmail.com,newpassword123"})
	void registerAndThenLogin_shouldSucceed(String email, String password) throws Exception {
		ResponseEntity<Void> registerResponse = performRegisterRequest(email, password);
		assertEquals(200, registerResponse.getStatusCode().value());

		ResponseEntity<String> loginResponse = performLoginRequest(email, password);
		assertEquals(200, loginResponse.getStatusCode().value());

		AuthResponse body = objectMapper.readValue(loginResponse.getBody(), AuthResponse.class);
		assertNotNull(body.token(), "Expected token after successful login");
	}

	@ParameterizedTest
	@CsvSource({"cryptobro@gmail.com,wrongpassword,401,", "notfound@example.com,anypassword,401,",
			"'',somepassword,400,email", "user@example.com,'',400,password", "'','',400,email|password",
			"invalid-email,password123,400,email"})
	void login_shouldFail_withInvalidInput(String email, String password, int expectedStatus,
			String expectedInvalidFieldsCsv) throws Exception {
		ResponseEntity<String> response = performLoginRequest(email, password);
		assertEquals(expectedStatus, response.getStatusCode().value());

		if (expectedStatus == 400) {
			Map<String, String> errors = parseErrorResponse(response);
			assertFalse(errors.isEmpty(), "Expected validation errors but got none");

			Set<String> expectedFields = Set.of(expectedInvalidFieldsCsv.split("\\|"));
			assertEquals(expectedFields, errors.keySet(), "Unexpected validation fields");
		}
	}

	private ResponseEntity<String> performLoginRequest(String email, String password) {
		AuthRequest request = new AuthRequest(email, password);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<AuthRequest> entity = new HttpEntity<>(request, headers);
		return restTemplate.exchange("/auth/login", HttpMethod.POST, entity, String.class);
	}

	private ResponseEntity<Void> performRegisterRequest(String email, String password) {
		CreateWalletRequest request = new CreateWalletRequest(email, password);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CreateWalletRequest> entity = new HttpEntity<>(request, headers);
		return restTemplate.exchange("/auth/register", HttpMethod.POST, entity, Void.class);
	}

	private Map<String, String> parseErrorResponse(ResponseEntity<String> response) throws Exception {
		ApiErrorResponse apiError = objectMapper.readValue(response.getBody(), ApiErrorResponse.class);
		return apiError.errors();
	}

}
