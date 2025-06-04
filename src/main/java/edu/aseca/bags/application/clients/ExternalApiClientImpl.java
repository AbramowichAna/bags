package edu.aseca.bags.application.clients;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.exception.UnsupportedExternalService;
import java.util.List;

public class ExternalApiClientImpl implements ExternalApiClient {

	private List<ExternalServiceClient> externalServiceClients;

	public ExternalApiClientImpl(List<ExternalServiceClient> externalServiceClients) {
		this.externalServiceClients = externalServiceClients;
	}

	@Override
	public boolean requestLoad(ExternalAccount service, Money amount) throws UnsupportedExternalService {
		for (ExternalServiceClient client : externalServiceClients) {
			if (client.supports(service.externalServiceName(), service.serviceType())) {
				return client.requestLoad(service, amount);
			}
		}
		throw new UnsupportedExternalService(service.externalServiceName());
	}

	@Override
	public boolean verifyCredentials(ExternalAccount service, String credentials) throws UnsupportedExternalService {
		for (ExternalServiceClient client : externalServiceClients) {
			if (client.supports(service.externalServiceName(), service.serviceType())) {
				return client.verifyCredentials(service, credentials);
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
