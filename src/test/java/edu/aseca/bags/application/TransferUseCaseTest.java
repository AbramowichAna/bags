package edu.aseca.bags.application;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.exception.InsufficientFundsException;
import edu.aseca.bags.exception.InvalidTransferException;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.testutil.TestWalletFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransferUseCaseTest {

	private TransferUseCase transferUseCase;
	private WalletRepository walletRepository;

	private TransferRepository transferRepository;

	@BeforeEach
	void setUp() {
		walletRepository = new InMemoryWalletRepository();
		transferRepository = new InMemoryTransferRepository();
		transferUseCase = new TransferUseCase(walletRepository, transferRepository);
	}

	@Test
	void successfulTransfer_001() {
		Wallet fromWallet = TestWalletFactory.createAndSave(walletRepository, "from@example.com", "password123", 500);
		Wallet toWallet = TestWalletFactory.createAndSave(walletRepository, "to@example.com", "password456", 100);
		Money transferAmount = Money.of(300);

		Email fromEmail = fromWallet.getEmail();
		Email toEmail = toWallet.getEmail();

		Transfer transfer = assertDoesNotThrow(() -> transferUseCase.execute(fromEmail, toEmail, transferAmount));

		assertNotNull(transfer);
		assertEquals(fromWallet, transfer.fromWallet());
		assertEquals(toWallet, transfer.toWallet());
		assertEquals(transferAmount, transfer.amount());
		assertNotNull(transfer.timestamp());

		Wallet updatedFromWallet = walletRepository.findByEmail(fromEmail).orElseThrow();
		Wallet updatedToWallet = walletRepository.findByEmail(toEmail).orElseThrow();

		assertEquals(Money.of(200), updatedFromWallet.getBalance());
		assertEquals(Money.of(400), updatedToWallet.getBalance());
	}

	@Test
	void throwsExceptionWhenFromWalletHasInsufficientFunds_002() {
		Money initialFromBalance = new Money(100);
		Money initialToBalance = new Money(50);

		Wallet fromWallet = TestWalletFactory.createAndSave(walletRepository, "from@example.com", "password123",
				initialFromBalance.amount());
		Wallet toWallet = TestWalletFactory.createAndSave(walletRepository, "to@example.com", "password456",
				initialToBalance.amount());

		Email fromEmail = fromWallet.getEmail();
		Email toEmail = toWallet.getEmail();

		Money transferAmount = Money.of(200);

		assertThrows(InsufficientFundsException.class,
				() -> transferUseCase.execute(fromEmail, toEmail, transferAmount));

		Wallet unchangedFromWallet = walletRepository.findByEmail(fromEmail).orElseThrow();
		Wallet unchangedToWallet = walletRepository.findByEmail(toEmail).orElseThrow();

		assertEquals(initialFromBalance, unchangedFromWallet.getBalance());
		assertEquals(initialToBalance, unchangedToWallet.getBalance());
	}

	@Test
	void throwsExceptionWhenFromWalletDoesNotExist_003() {
		Email nonExistentEmail = new Email("nonexistent@example.com");

		Wallet toWallet = TestWalletFactory.createAndSave(walletRepository, "to@example.com", "password456", 50);
		Email toEmail = toWallet.getEmail();

		Money transferAmount = Money.of(100);

		assertThrows(WalletNotFoundException.class,
				() -> transferUseCase.execute(nonExistentEmail, toEmail, transferAmount));
	}

	@Test
	void throwsExceptionWhenToWalletDoesNotExist_004() {
		Wallet fromWallet = TestWalletFactory.createAndSave(walletRepository, "from@example.com", "password123", 200);
		Email fromEmail = fromWallet.getEmail();

		Email nonExistentEmail = new Email("nonexistent@example.com");
		Money transferAmount = Money.of(100);

		assertThrows(WalletNotFoundException.class,
				() -> transferUseCase.execute(fromEmail, nonExistentEmail, transferAmount));

		Wallet unchangedFromWallet = walletRepository.findByEmail(fromEmail).orElseThrow();
		assertEquals(Money.of(200), unchangedFromWallet.getBalance());
	}

	@Test
	void throwsExceptionWhenOneWalletTriesToTransferToItself_005() {
		Wallet fromWallet = TestWalletFactory.createAndSave(walletRepository, "from@example.com", "password123", 200);

		assertThrows(InvalidTransferException.class,
				() -> transferUseCase.execute(fromWallet.getEmail(), fromWallet.getEmail(), Money.of(100)));
	}
}