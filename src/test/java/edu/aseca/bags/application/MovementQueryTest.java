package edu.aseca.bags.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.aseca.bags.application.dto.MovementView;
import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.interfaces.WalletRepository;
import edu.aseca.bags.application.queries.MovementQuery;
import edu.aseca.bags.application.util.InMemoryMovementRepository;
import edu.aseca.bags.application.util.InMemoryWalletRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.domain.transaction.Movement;
import edu.aseca.bags.domain.transaction.MovementType;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.testutil.Defaults;
import edu.aseca.bags.testutil.TestWalletFactory;
import java.time.Instant;
import org.junit.jupiter.api.Test;

public class MovementQueryTest {

	InMemoryMovementRepository inMemoryMovementRepository = new InMemoryMovementRepository();
	WalletRepository walletRepository = new InMemoryWalletRepository();
	MovementQuery movementQuery = new MovementQuery(inMemoryMovementRepository);

	@Test
	void shouldReturnAllMovementsForParticipant_001() {

		String fromEmail = "from@gmail.com";
		Participant from = TestWalletFactory.createAndSave(walletRepository, fromEmail, "password", 100);
		Participant to = TestWalletFactory.createAndSave(walletRepository, "to@gmail.com", "password", 100);

		Movement movement = new Movement(TransferNumber.random(), from, to, Instant.now(), Money.of(50),
				MovementType.TRANSFER);

		inMemoryMovementRepository.save(movement);

		var movementPage = movementQuery.getMovements(from, new Pagination(0, 10));

		assertEquals(1, movementPage.getTotalElements(), "Should return one element");
		assertEquals(1, movementPage.getTotalPages(), "Should return one page");
		assertEquals(1, movementPage.getContent().size(), "Should return one movement in content");
		MovementView first = movementPage.getContent().getFirst();
		compareMovements(movement, first);
		assertEquals("OUT", first.type(), "Movement type should be OUT for transfers to the wallet");
	}

	private static void compareMovements(Movement movement, MovementView movementPageItem) {
		assertEquals(movement.movementId().value().toString(), movementPageItem.id(),
				"Returned movement should match the saved movement");
		assertEquals(movement.from().getEmail().address(), movementPageItem.fromParticipant().email(),
				"From email should match");
		assertEquals(movement.to().getEmail().address(), movementPageItem.toParticipant().email(),
				"To email should match");
		assertEquals(movement.amount().amount(), movementPageItem.amount().doubleValue(), "Amount should match");
	}

	@Test
	void shouldReturnNoMovementsForParticipant_002() {

		String fromEmail = "from@gmail.com";
		Participant p = TestWalletFactory.createAndSave(walletRepository, fromEmail, "password", 100);

		var movementPage = movementQuery.getMovements(p, new Pagination(0, 10));

		assertEquals(0, movementPage.getTotalElements(), "Should return zero element");
		assertEquals(0, movementPage.getTotalPages(), "Should return zero page");
		assertEquals(0, movementPage.getContent().size(), "Should return zero movement in content");
	}

	@Test
	void shouldReturnNoMovementsForNotPersistedParticipant_003() {

		String fromEmail = "from@gmail.com";
		Participant notP = TestWalletFactory.createWallet(fromEmail, Defaults.getDefaultPassword().hash());
		var movementPage = movementQuery.getMovements(notP, new Pagination(0, 10));
		assertEquals(0, movementPage.getTotalElements(), "Should return zero element");
		assertEquals(0, movementPage.getTotalPages(), "Should return zero page");
		assertEquals(0, movementPage.getContent().size(), "Should return zero movement in content");

	}

}
