package edu.aseca.bags.domain.transaction;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.domain.participant.Wallet;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ExternalLoadTest {

	@Test
	void shouldCreateExternalLoadSuccessfully_001() {
		Wallet wallet = new Wallet(new Email("user@gmail.com"), new Password("hola12345"));
		Money amount = new Money(100.0);
		Instant timestamp = Instant.now();

		ExternalAccount externalAccount = new ExternalAccount("BANK_TRANSFER", ServiceType.BANK, "bank@bank.com");

		ExternalLoad load = new ExternalLoad(UUID.randomUUID(), wallet, amount, timestamp, externalAccount);

		assertNotNull(load.transactionId());
		assertEquals(wallet, load.toWallet());
		assertEquals(amount, load.amount());
		assertEquals(timestamp, load.timestamp());
		assertEquals(externalAccount, load.externalAccount());
	}

	@Test
	void shouldThrowOnNullArguments_002() {
		Wallet wallet = new Wallet(new Email("user@gmail.com"), new Password("hola12345"));
		Money amount = new Money(100.0);
		Instant timestamp = Instant.now();
		ExternalAccount externalAccount = new ExternalAccount("BANK_TRANSFER", ServiceType.BANK, "bank@bank.com");

		assertThrows(NullPointerException.class,
				() -> new ExternalLoad(null, wallet, amount, timestamp, externalAccount));
		assertThrows(NullPointerException.class,
				() -> new ExternalLoad(UUID.randomUUID(), null, amount, timestamp, externalAccount));
		assertThrows(NullPointerException.class,
				() -> new ExternalLoad(UUID.randomUUID(), wallet, null, timestamp, externalAccount));
		assertThrows(NullPointerException.class,
				() -> new ExternalLoad(UUID.randomUUID(), wallet, amount, null, externalAccount));
		assertThrows(NullPointerException.class,
				() -> new ExternalLoad(UUID.randomUUID(), wallet, amount, timestamp, null));
	}
}