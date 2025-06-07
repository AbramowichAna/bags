package edu.aseca.bags.domain.transaction;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.testutil.TestWalletFactory;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class MovementTest {

	@Test
	public void shouldCreateMovementSuccessfully_001() {
		Participant from = TestWalletFactory.createWallet("from@gmail.com", somePassword());
		Participant to = TestWalletFactory.createWallet("to@gmail.com", somePassword());
		Money amount = getAmount();
		Instant timestamp = getNow();
		TransferNumber transferNumber = TransferNumber.random();

		Movement movement = new Movement(transferNumber, from, to, timestamp, amount, MovementType.TRANSFER);

		assertNotNull(movement.movementId());
		assertEquals(from, movement.from());
		assertEquals(to, movement.to());
		assertEquals(amount, movement.amount());
		assertEquals(timestamp, movement.timestamp());
	}

	@Test
	public void shouldThrowOnNullArguments_002() {
		Participant from = TestWalletFactory.createWallet("from@gmail.com", somePassword());
		Participant to = TestWalletFactory.createWallet("to@gmail.com", somePassword());
		Money amount = getAmount();
		Instant timestamp = getNow();
		TransferNumber transferNumber = TransferNumber.random();

		assertThrows(NullPointerException.class,
				() -> new Movement(null, from, to, timestamp, amount, MovementType.TRANSFER));
		assertThrows(NullPointerException.class,
				() -> new Movement(transferNumber, null, to, timestamp, amount, MovementType.TRANSFER));
		assertThrows(NullPointerException.class,
				() -> new Movement(transferNumber, from, null, timestamp, amount, MovementType.TRANSFER));
		assertThrows(NullPointerException.class,
				() -> new Movement(transferNumber, from, to, null, amount, MovementType.TRANSFER));
		assertThrows(NullPointerException.class,
				() -> new Movement(transferNumber, from, to, timestamp, null, MovementType.TRANSFER));
	}

	@Test
	public void shouldVerifyEqualityWhenAllAttributesAreEqual_003() {
		Participant from = TestWalletFactory.createWallet("from@gmail.com", somePassword());
		Participant to = TestWalletFactory.createWallet("to@gmail.com", somePassword());
		Money amount = getAmount();
		Instant timestamp = getNow();
		TransferNumber transferNumber = TransferNumber.random();

		Movement movement1 = new Movement(transferNumber, from, to, timestamp, amount, MovementType.TRANSFER);
		Movement movement2 = new Movement(transferNumber, from, to, timestamp, amount, MovementType.TRANSFER);

		assertEquals(movement1, movement2);
		assertEquals(movement1.hashCode(), movement2.hashCode());
	}

	private static Instant getNow() {
		return Instant.now();
	}

	@NotNull
	private static Money getAmount() {
		return new Money(50.0);
	}

	@NotNull
	private static String somePassword() {
		return "password123";
	}
}