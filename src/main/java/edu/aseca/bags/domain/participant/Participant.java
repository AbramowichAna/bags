package edu.aseca.bags.domain.participant;

import edu.aseca.bags.domain.email.Email;

public interface Participant {
	String getServiceName();

	ServiceType getServiceType();

	Email getEmail();
}
