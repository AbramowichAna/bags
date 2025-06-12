package edu.aseca.bags.application.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.exception.ExternalEntityNotFoundException;
import edu.aseca.bags.exception.ExternalServiceException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.client.RestClient;

public class BankClient implements ExternalServiceClient {

	private final RestClient restClient;
	private final String bankApiUrl;

	public BankClient(RestClient restTemplate, String bankApiUrl) {
		this.restClient = restTemplate;
		this.bankApiUrl = bankApiUrl;
	}

	@Override
	public boolean supports(String serviceName, ServiceType type) {
		return "Bank".equalsIgnoreCase(serviceName) && type == ServiceType.BANK;
	}

	@Override
	public boolean requestLoad(ExternalAccount service, Money amount, Email walletEmail) {
		String url = bankApiUrl + "/debin";

		Map<String, Object> body = Map.of("walletId", walletEmail.address(), "bankAccountId", service.email().address(),
				"amount", amount.amount());

		ObjectMapper mapper = new ObjectMapper();
		restClient.post().uri(url).contentType(MediaType.APPLICATION_JSON).body(body).retrieve()
				.onStatus(HttpStatus.BAD_REQUEST::equals, (request, response) -> {
					String message = "";
					try (var is = response.getBody()) {
						String errorJson = new String(is.readAllBytes(), StandardCharsets.UTF_8);
						System.out.println("Error response from Bank API: " + errorJson);
						message = mapper.readTree(errorJson).path("error").asText("Unknown error");
					} catch (Exception ignored) {
						throw new IllegalArgumentException("Invalid request to Bank API: " + message);
					}
					throw new IllegalArgumentException("Invalid request to Bank API: " + message);
				}).onStatus(HttpStatus.NOT_FOUND::equals, (req, res) -> {
					throw new ExternalEntityNotFoundException();
				}).onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
					throw new ExternalServiceException("Bank API internal error");
				}).toBodilessEntity();
		return true;
	}
}