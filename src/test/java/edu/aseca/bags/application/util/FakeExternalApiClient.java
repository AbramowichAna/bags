package edu.aseca.bags.application.util;

import edu.aseca.bags.application.clients.ExternalApiClient;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.exception.UnsupportedExternalService;
import java.util.*;

public class FakeExternalApiClient implements ExternalApiClient {

	private final Set<String> knownServices;
	private Set<ExternalAccount> requests;

	public FakeExternalApiClient() {
		knownServices = Set.of("Bank", "PayPal", "CryptoExchange");
		requests = new HashSet<>();
	}

	@Override
	public boolean requestLoad(ExternalAccount service, Money amount, Email walletEmail)
			throws UnsupportedExternalService {
		if (!knownServices.contains(service.externalServiceName())) {
			throw new UnsupportedExternalService(service.externalServiceName());
		}
		requests.add(service);
		return requests.contains(service);
	}

	public boolean receivedRequest(ExternalAccount service) {
		return requests.contains(service);
	}

	@Override
	public boolean isSupportedService(String serviceName, ServiceType type) {
		return knownServices.contains(serviceName);
	}
}
