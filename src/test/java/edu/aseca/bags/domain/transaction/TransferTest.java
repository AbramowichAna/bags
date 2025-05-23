package edu.aseca.bags.domain.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.wallet.Wallet;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransferTest {

	private Wallet fromWallet;
	private Wallet toWallet;
	private Money validAmount;
	private Instant timestamp;

	@BeforeEach
	void setUp() {
		Email fromEmail = new Email("sender@example.com");
		Email toEmail = new Email("receiver@example.com");
		Password password = new Password("hashedPassword123");

		fromWallet = new Wallet(fromEmail, password);
		toWallet = new Wallet(toEmail, password);
		validAmount = new Money(100.0);
		timestamp = Instant.now();

		fromWallet.addBalance(new Money(200.0));
	}

	@Test
	void testCreateValidTransfer_001() {
		Transfer transfer = new Transfer(fromWallet, toWallet, validAmount, timestamp);
		assertNotNull(transfer);
		assertEquals(fromWallet, transfer.fromWallet());
		assertEquals(toWallet, transfer.toWallet());
		assertEquals(validAmount, transfer.amount());
		assertEquals(timestamp, transfer.timestamp());
	}

	@Test
	void testTransferWithNullFromWallet_002() {
		assertThrows(NullPointerException.class, () -> new Transfer(null, toWallet, validAmount, timestamp));
	}

	@Test
	void testTransferWithNullToWallet_003() {
		// Then
		assertThrows(NullPointerException.class, () -> new Transfer(fromWallet, null, validAmount, timestamp));
	}

	@Test
	void testTransferWithNullAmount_004() {
		assertThrows(NullPointerException.class, () -> new Transfer(fromWallet, toWallet, null, timestamp));
	}

	@Test
	void testTransferWithNullTimestamp_005() {
		assertThrows(NullPointerException.class, () -> new Transfer(fromWallet, toWallet, validAmount, null));
	}

	@Test
	void testTransferWithInsufficientBalance_006() {
		Wallet emptyWallet = new Wallet(new Email("empty@example.com"), new Password("pass123"));
		Money largeAmount = new Money(50.0);

		Transfer transfer = new Transfer(emptyWallet, toWallet, largeAmount, timestamp);
		assertNotNull(transfer);

		assertThrows(IllegalStateException.class, () -> emptyWallet.subtractBalance(largeAmount));
	}

	@Test
	void testTransferWithSufficientBalance_007() {
		Money transferAmount = new Money(50.0);

		Transfer transfer = new Transfer(fromWallet, toWallet, transferAmount, timestamp);

		assertNotNull(transfer);

		double initialFromBalance = fromWallet.getBalance().amount();
		double initialToBalance = toWallet.getBalance().amount();

		fromWallet.subtractBalance(transferAmount);
		toWallet.addBalance(transferAmount);

		assertEquals(initialFromBalance - transferAmount.amount(), fromWallet.getBalance().amount());
		assertEquals(initialToBalance + transferAmount.amount(), toWallet.getBalance().amount());
	}

	@Test
	void testTransferEquality_008() {
		Transfer transfer1 = new Transfer(fromWallet, toWallet, validAmount, timestamp);
		Transfer transfer2 = new Transfer(fromWallet, toWallet, validAmount, timestamp);

		assertEquals(transfer1, transfer2);
		assertEquals(transfer1.hashCode(), transfer2.hashCode());
	}
}