package edu.aseca.bags.application.clients;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.exception.ExternalEntityNotFoundException;
import edu.aseca.bags.exception.ExternalServiceException;
import java.util.HashMap;
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

		restClient.post().uri(url).contentType(MediaType.APPLICATION_JSON).body(body).retrieve()
				.onStatus(HttpStatus.BAD_REQUEST::equals, (req, res) -> {
					throw new IllegalArgumentException("Invalid request to Bank API: HTTP 400");
				}).onStatus(HttpStatus.NOT_FOUND::equals, (req, res) -> {
					throw new ExternalEntityNotFoundException();
				}).onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
					throw new ExternalServiceException("Bank API internal error");
				}).toBodilessEntity();
		return true;
	}
}