package edu.aseca.bags.application.clients;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.exception.UnsupportedExternalService;

public interface ExternalApiClient {
	boolean requestLoad(ExternalAccount service, Money amount, Email walletEmail) throws UnsupportedExternalService;

	boolean isSupportedService(String serviceName, ServiceType type);
}