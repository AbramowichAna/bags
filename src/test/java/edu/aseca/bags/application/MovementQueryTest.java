package edu.aseca.bags.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.aseca.bags.application.dto.MovementView;
import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.interfaces.WalletRepository;
import edu.aseca.bags.application.queries.MovementQuery;
import edu.aseca.bags.application.util.InMemoryMovementRepository;
import edu.aseca.bags.application.util.InMemoryWalletRepository;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.domain.transaction.Movement;
import edu.aseca.bags.domain.transaction.MovementId;
import edu.aseca.bags.domain.transaction.MovementType;
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

		Movement movement = new Movement(MovementId.random(), from, to, Instant.now(), Money.of(50),
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

	@Test
	void shouldPaginateMovementsCorrectly_004() {
		String email = "user@gmail.com";
		Participant user = TestWalletFactory.createAndSave(walletRepository, email, "password", 100);

		for (int i = 0; i < 15; i++) {
			Movement m = new Movement(MovementId.random(), user,
					TestWalletFactory.createAndSave(walletRepository, "to" + i + "@gmail.com", "pw", 100),
					Instant.now().minusSeconds(i), Money.of(10), MovementType.TRANSFER);
			inMemoryMovementRepository.save(m);
		}

		var firstPage = movementQuery.getMovements(user, new Pagination(0, 10));
		var secondPage = movementQuery.getMovements(user, new Pagination(1, 10));

		assertEquals(10, firstPage.getContent().size(), "First page should have 10 movements");
		assertEquals(5, secondPage.getContent().size(), "Second page should have 5 movements");
		assertEquals(2, firstPage.getTotalPages(), "Should be 2 pages");
	}

	@Test
	void shouldReturnEmptyPageWhenPageIndexTooHigh_005() {
		String email = "user@gmail.com";
		Participant user = TestWalletFactory.createAndSave(walletRepository, email, "password", 100);

		for (int i = 0; i < 3; i++) {
			Movement m = new Movement(MovementId.random(), user,
					TestWalletFactory.createAndSave(walletRepository, "to" + i + "@gmail.com", "pw", 100),
					Instant.now(), Money.of(10), MovementType.TRANSFER);
			inMemoryMovementRepository.save(m);
		}

		var page = movementQuery.getMovements(user, new Pagination(2, 2));
		assertEquals(0, page.getContent().size(), "No movements should be returned for out-of-range page");
	}

	@Test
	void shouldReturnOnlyMovementsForGivenParticipant_006() {
		Participant user1 = TestWalletFactory.createAndSave(walletRepository, "user1@gmail.com", "pw", 100);
		Participant user2 = TestWalletFactory.createAndSave(walletRepository, "user2@gmail.com", "pw", 100);

		inMemoryMovementRepository.save(
				new Movement(MovementId.random(), user1, user2, Instant.now(), Money.of(10), MovementType.TRANSFER));
		inMemoryMovementRepository.save(
				new Movement(MovementId.random(), user2, user1, Instant.now(), Money.of(20), MovementType.TRANSFER));

		var page1 = movementQuery.getMovements(user1, new Pagination(0, 10));
		var page2 = movementQuery.getMovements(user2, new Pagination(0, 10));

		assertEquals(2, page1.getTotalElements(), "user1 should see both movements");
		assertEquals(2, page2.getTotalElements(), "user2 should see both movements");
	}

	@Test
	void shouldSetMovementTypeCorrectly_007() {
		Participant from = TestWalletFactory.createAndSave(walletRepository, "from@gmail.com", "pw", 100);
		Participant to = TestWalletFactory.createAndSave(walletRepository, "to@gmail.com", "pw", 100);

		Movement movement = new Movement(MovementId.random(), from, to, Instant.now(), Money.of(10),
				MovementType.TRANSFER);
		inMemoryMovementRepository.save(movement);

		var fromPage = movementQuery.getMovements(from, new Pagination(0, 10));
		var toPage = movementQuery.getMovements(to, new Pagination(0, 10));

		assertEquals("OUT", fromPage.getContent().getFirst().type(), "Should be OUT for sender");
		assertEquals("IN", toPage.getContent().getFirst().type(), "Should be IN for receiver");
	}

}
