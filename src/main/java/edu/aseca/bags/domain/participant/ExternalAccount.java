package edu.aseca.bags.domain.participant;

import edu.aseca.bags.domain.email.Email;
import java.util.Objects;

public record ExternalAccount(String externalServiceName, ServiceType serviceType, Email email) implements Participant {
	public ExternalAccount {
		Objects.requireNonNull(externalServiceName, "Name of external service must not be null");
		Objects.requireNonNull(serviceType, "Type of external service must not be null");
		Objects.requireNonNull(email, "Email must not be null");
	}

	@Override
	public String getServiceName() {
		return externalServiceName;
	}

	@Override
	public ServiceType getServiceType() {
		return serviceType;
	}

	@Override
	public Email getEmail() {
		return email;
	}
}