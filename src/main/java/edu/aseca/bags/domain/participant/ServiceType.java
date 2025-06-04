package edu.aseca.bags.domain.participant;

public enum ServiceType {
	BANK, CREDIT_CARD, DEBIT_CARD, VIRTUAL_WALLET;

	public static ServiceType fromString(String service) {
		for (ServiceType serviceType : values()) {
			if (serviceType.name().equalsIgnoreCase(service)) {
				return serviceType;
			}
		}
		throw new IllegalArgumentException("Unknown external service: " + service);
	}
}
