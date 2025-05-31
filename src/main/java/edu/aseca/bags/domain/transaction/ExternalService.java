package edu.aseca.bags.domain.transaction;

public enum ExternalService {
	BANK_TRANSFER, CREDIT_CARD;

	public static ExternalService fromString(String service) {
		for (ExternalService externalService : values()) {
			if (externalService.name().equalsIgnoreCase(service)) {
				return externalService;
			}
		}
		throw new IllegalArgumentException("Unknown external service: " + service);
	}
}
