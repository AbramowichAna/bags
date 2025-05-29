package edu.aseca.bags.application;

import static edu.aseca.bags.testutil.TestWalletFactory.createAndSave;
import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.exception.InsufficientFundsException;
import edu.aseca.bags.exception.InvalidTransferException;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.testutil.TestWalletFactory;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransferUseCaseTest {

	private TransferUseCase transferUseCase;
	private WalletRepository walletRepository;

	private TransferRepository transferRepository;

	private final UUID knownUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	private final TransferNumber knownTransferNumber = TransferNumber.of(knownUuid);
	private static final String FROM_EMAIL = "from@example.com";
	private static final String TO_EMAIL = "to@example.com";

	@BeforeEach
	void setUp() {
		walletRepository = new InMemoryWalletRepository();
		transferRepository = new InMemoryTransferRepository();
		var gen = new KnownTransferNumberGenerator(knownTransferNumber);
		transferUseCase = new TransferUseCase(walletRepository, transferRepository, gen);
	}

	@Test
	void successfulTransfer_001() {

		Wallet fromWallet = TestWalletFactory.createAndSave(walletRepository, FROM_EMAIL, "password123", 500);
		Wallet toWallet = TestWalletFactory.createAndSave(walletRepository, TO_EMAIL, "password456", 100);
		Money transferAmount = Money.of(300);

		Email fromEmail = fromWallet.getEmail();
		Email toEmail = toWallet.getEmail();

		Transfer transfer = assertDoesNotThrow(() -> transferUseCase.execute(fromEmail, toEmail, transferAmount));

		assertTransferDetails(transfer, fromWallet, toWallet, transferAmount);

		assertBalances(fromEmail, Money.of(200));
		assertBalances(toEmail, Money.of(400));

		assertTransferStored(transfer);
	}

	@Test
	void throwsExceptionWhenFromWalletHasInsufficientFunds_002() {
		Money initialFromBalance = new Money(100);
		Money initialToBalance = new Money(50);

		Wallet fromWallet = TestWalletFactory.createAndSave(walletRepository, FROM_EMAIL, "password123",
				initialFromBalance.amount());
		Wallet toWallet = TestWalletFactory.createAndSave(walletRepository, TO_EMAIL, "password456",
				initialToBalance.amount());

		Email fromEmail = fromWallet.getEmail();
		Email toEmail = toWallet.getEmail();

		Money transferAmount = Money.of(200);

		assertThrows(InsufficientFundsException.class,
				() -> transferUseCase.execute(fromEmail, toEmail, transferAmount));

		assertBalances(fromEmail, initialFromBalance);
		assertBalances(toEmail, initialToBalance);
	}

	@Test
	void throwsExceptionWhenFromWalletDoesNotExist_003() {
		Email nonExistentEmail = new Email("nonexistent@example.com");

		Wallet toWallet = TestWalletFactory.createAndSave(walletRepository, TO_EMAIL, "password456", 50);
		Email toEmail = toWallet.getEmail();

		Money transferAmount = Money.of(100);

		assertThrows(WalletNotFoundException.class,
				() -> transferUseCase.execute(nonExistentEmail, toEmail, transferAmount));
	}

	@Test
	void throwsExceptionWhenToWalletDoesNotExist_004() {
		Wallet fromWallet = TestWalletFactory.createAndSave(walletRepository, FROM_EMAIL, "password123", 200);
		Email fromEmail = fromWallet.getEmail();

		Email nonExistentEmail = new Email("nonexistent@example.com");
		Money transferAmount = Money.of(100);

		assertThrows(WalletNotFoundException.class,
				() -> transferUseCase.execute(fromEmail, nonExistentEmail, transferAmount));

		assertBalances(fromEmail, Money.of(200));
	}

	@Test
	void throwsExceptionWhenOneWalletTriesToTransferToItself_005() {
		Wallet fromWallet = TestWalletFactory.createAndSave(walletRepository, FROM_EMAIL, "password123", 200);

		assertThrows(InvalidTransferException.class,
				() -> transferUseCase.execute(fromWallet.getEmail(), fromWallet.getEmail(), Money.of(100)));
	}

	@Test
	void throwsExceptionWhenTransferAmountIsZero_006() {
		Wallet fromWallet = createAndSave(walletRepository, FROM_EMAIL, "password123", 200);
		Wallet toWallet = createAndSave(walletRepository, TO_EMAIL, "password456", 100);

		Email fromEmail = fromWallet.getEmail();
		Email toEmail = toWallet.getEmail();

		assertThrows(InvalidTransferException.class, () -> transferUseCase.execute(fromEmail, toEmail, Money.of(0)));
	}

	private void assertBalances(Email fromEmail, Money amount) {

		Wallet updatedFromWallet = walletRepository.findByEmail(fromEmail).orElseThrow();
		assertEquals(amount, updatedFromWallet.getBalance());
	}

	private void assertTransferStored(Transfer transfer) {
		Transfer foundTransfer = assertDoesNotThrow(() -> transferRepository.findByTransferNumber(knownTransferNumber)
				.orElseThrow(() -> new RuntimeException("Transfer not found in repository")));

		assertEquals(transfer, foundTransfer);
	}

	private static void assertTransferDetails(Transfer transfer, Wallet fromWallet, Wallet toWallet,
			Money transferAmount) {
		assertNotNull(transfer);
		assertEquals(fromWallet, transfer.fromWallet());
		assertEquals(toWallet, transfer.toWallet());
		assertEquals(transferAmount, transfer.amount());
		assertNotNull(transfer.timestamp());
	}
}