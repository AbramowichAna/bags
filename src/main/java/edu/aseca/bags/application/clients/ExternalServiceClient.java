package edu.aseca.bags.application.clients;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;

public interface ExternalServiceClient {
	boolean supports(String serviceName, ServiceType type);

	boolean requestLoad(ExternalAccount service, Money amount);

	boolean verifyCredentials(ExternalAccount service, String credentials);
}