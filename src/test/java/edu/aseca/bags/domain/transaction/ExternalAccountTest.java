package edu.aseca.bags.domain.transaction;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.testutil.Defaults;
import edu.aseca.bags.testutil.TestExternalAccountFactory;
import org.junit.jupiter.api.Test;

class ExternalAccountTest {

	@Test
	void shouldCreateExternalServiceSuccessfully_001() {
		ExternalAccount service = TestExternalAccountFactory.createExternalAccount("Banco Galicia", ServiceType.BANK,
				"user@email.com");
		assertEquals("Banco Galicia", service.externalServiceName());
		assertEquals(ServiceType.BANK, service.serviceType());
		assertEquals("user@email.com", service.email().address());
	}

	@Test
	void shouldThrowOnNullName_002() {
		assertThrows(NullPointerException.class,
				() -> TestExternalAccountFactory.createExternalAccount(null, ServiceType.BANK, "user@email.com"));
	}

	@Test
	void shouldThrowOnNullServiceType_003() {
		assertThrows(NullPointerException.class, () -> TestExternalAccountFactory.createExternalAccount("Banco Macro",
				null, Defaults.getDefaultEmail().address()));
	}
}