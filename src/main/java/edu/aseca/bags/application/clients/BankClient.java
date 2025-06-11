package edu.aseca.bags.application.clients;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.exception.ExternalServiceException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class BankClient implements ExternalServiceClient {

	private final RestTemplate restTemplate;
	private final String bankApiUrl;

	public BankClient(RestTemplate restTemplate, String bankApiUrl) {
		this.restTemplate = restTemplate;
		this.bankApiUrl = bankApiUrl;
	}

	@Override
	public boolean supports(String serviceName, ServiceType type) {
		return "Bank".equalsIgnoreCase(serviceName) && type == ServiceType.BANK;
	}

	@Override
	public boolean requestLoad(ExternalAccount service, Money amount, Email walletEmail) {
		String url = bankApiUrl + "/debin";
		Map<String, Object> body = new HashMap<>();
		body.put("walletId", walletEmail.address());
		body.put("bankAccountId", service.email().address());
		body.put("amount", amount.amount());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
			if (!response.getStatusCode().is2xxSuccessful()) {
				throw new ExternalServiceException("Bank API returned status: " + response.getStatusCode());
			}
			return true;
		} catch (Exception e) {
			throw new ExternalServiceException("Error communicating with Bank API", e);
		}
	}
}