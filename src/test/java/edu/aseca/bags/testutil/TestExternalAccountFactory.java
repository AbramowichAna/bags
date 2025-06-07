package edu.aseca.bags.testutil;

import edu.aseca.bags.application.interfaces.ExternalAccountRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;

public class TestExternalAccountFactory {

	public static ExternalAccount createExternalAccount(String serviceName, ServiceType serviceType, String email) {
		return new ExternalAccount(serviceName, serviceType, new Email(email));
	}
}
