package edu.aseca.bags.application;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.interfaces.WalletRepository;
import edu.aseca.bags.application.queries.MovementQuery;
import edu.aseca.bags.application.queries.WalletMovementsQuery;
import edu.aseca.bags.application.util.InMemoryMovementRepository;
import edu.aseca.bags.application.util.InMemoryWalletRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.domain.transaction.Movement;
import edu.aseca.bags.domain.transaction.MovementId;
import edu.aseca.bags.domain.transaction.MovementType;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.testutil.TestWalletFactory;
import java.time.Instant;
import org.junit.jupiter.api.Test;

public class WalletMovementsQueryTest {

	// Si existe la wallet, que los obtenga, si no existe que tire un error, que me
	// de si soy from y to
	InMemoryMovementRepository inMemoryMovementRepository = new InMemoryMovementRepository();
	WalletRepository walletRepository = new InMemoryWalletRepository();
	MovementQuery movementQuery = new MovementQuery(inMemoryMovementRepository);
	WalletMovementsQuery walletMovementsQuery = new WalletMovementsQuery(walletRepository, movementQuery);

	@Test
	void shouldReturnMovementsForExistingWallet() {

		String fromEmail = "from@gmail.com";
		Participant from = TestWalletFactory.createAndSave(walletRepository, fromEmail, "password", 100);
		Participant to = TestWalletFactory.createAndSave(walletRepository, "to@gmail.com", "password", 100);

		Movement movement = new Movement(MovementId.random(), from, to, Instant.now(), Money.of(50),
				MovementType.TRANSFER);

		inMemoryMovementRepository.save(movement);

		assertDoesNotThrow(() -> {
			walletMovementsQuery.getMovements(new Email(fromEmail), new Pagination(0, 10));
		});
	}

	@Test
	void shouldFailWhenWalletDoesNotExist() {
		String fromEmail = "from@gmail.com";

		assertThrows(WalletNotFoundException.class, () -> {
			walletMovementsQuery.getMovements(new Email(fromEmail), new Pagination(0, 10));
		});
	}
}
