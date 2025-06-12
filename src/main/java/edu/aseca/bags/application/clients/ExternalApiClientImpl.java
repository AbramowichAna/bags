package edu.aseca.bags.application.clients;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.exception.UnsupportedExternalService;
import java.util.List;
import java.util.Objects;

public class ExternalApiClientImpl implements ExternalApiClient {

	private final List<ExternalServiceClient> externalServiceClients;

	public ExternalApiClientImpl(List<ExternalServiceClient> externalServiceClients) {
		this.externalServiceClients = Objects.requireNonNull(externalServiceClients);
	}

	@Override
	public boolean requestLoad(ExternalAccount service, Money amount, Email walletEmail)
			throws UnsupportedExternalService {
		for (ExternalServiceClient client : externalServiceClients) {
			if (client.supports(service.externalServiceName(), service.serviceType())) {
				return client.requestLoad(service, amount, walletEmail);
			}
		}
		throw new UnsupportedExternalService(service.externalServiceName());
	}

	@Override
	public boolean isSupportedService(String serviceName, ServiceType type) {
		for (ExternalServiceClient client : externalServiceClients) {
			if (client.supports(serviceName, type)) {
				return true;
			}
		}
		return false;
	}
}