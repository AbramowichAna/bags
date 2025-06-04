package edu.aseca.bags.domain.transaction;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import org.junit.jupiter.api.Test;

class ExternalAccountTest {

	@Test
	void shouldCreateExternalServiceSuccessfully_001() {
		ExternalAccount service = new ExternalAccount("Banco Galicia", ServiceType.BANK, "user@email.com");
		assertEquals("Banco Galicia", service.externalServiceName());
		assertEquals(ServiceType.BANK, service.serviceType());
		assertEquals("user@email.com", service.email());
	}

	@Test
	void shouldThrowOnNullName_002() {
		assertThrows(NullPointerException.class, () -> new ExternalAccount(null, ServiceType.BANK, "user@email.com"));
	}

	@Test
	void shouldThrowOnNullServiceType_003() {
		assertThrows(NullPointerException.class, () -> new ExternalAccount("Paypal", null, "user@email.com"));
	}
}