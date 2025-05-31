package edu.aseca.bags.domain.transaction;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.wallet.Wallet;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ExternalLoadTest {

	@Test
	void shouldCreateExternalLoadSuccessfully_001() {
		Wallet wallet = new Wallet(new Email("user@gmail.com"), new Password("hola12345"));
		Money amount = new Money(100.0);
		Instant timestamp = Instant.now();

		ExternalLoad load = new ExternalLoad(UUID.randomUUID(), wallet, amount, timestamp,
				ExternalService.BANK_TRANSFER);

		assertNotNull(load.transactionId());
		assertEquals(wallet, load.toWallet());
		assertEquals(amount, load.amount());
		assertEquals(timestamp, load.timestamp());
	}

	@Test
	void shouldThrowOnNullArguments_002() {
		Wallet wallet = new Wallet(new Email("user@gmail.com"), new Password("hola12345"));
		Money amount = new Money(100.0);
		Instant timestamp = Instant.now();

		assertThrows(NullPointerException.class,
				() -> new ExternalLoad(null, wallet, amount, timestamp, ExternalService.BANK_TRANSFER));
		assertThrows(NullPointerException.class,
				() -> new ExternalLoad(UUID.randomUUID(), null, amount, timestamp, ExternalService.BANK_TRANSFER));
		assertThrows(NullPointerException.class,
				() -> new ExternalLoad(UUID.randomUUID(), wallet, null, timestamp, ExternalService.BANK_TRANSFER));
		assertThrows(NullPointerException.class,
				() -> new ExternalLoad(UUID.randomUUID(), wallet, amount, null, ExternalService.BANK_TRANSFER));
	}
}