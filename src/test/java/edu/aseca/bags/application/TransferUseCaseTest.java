package edu.aseca.bags.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.exception.InsufficientFundsException;
import edu.aseca.bags.exception.WalletNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransferUseCaseTest {

	private TransferUseCase transferUseCase;
	private WalletRepository walletRepository;

	@BeforeEach
	void setUp() {
		walletRepository = new InMemoryWalletRepository();
		TransferRepository transferRepository = new InMemoryTransferRepository();
		transferUseCase = new TransferUseCase(walletRepository, transferRepository);
	}

	@Test
	void successfulTransfer_001() throws WalletNotFoundException, InsufficientFundsException {
		Email fromEmail = new Email("from@example.com");
		Email toEmail = new Email("to@example.com");
		Password fromPassword = new Password("password123");
		Password toPassword = new Password("password456");
		Money initialFromBalance = Money.of(500);
		Money initialToBalance = Money.of(100);
		Money transferAmount = Money.of(300);

		Wallet fromWallet = new Wallet(fromEmail, fromPassword);
		fromWallet.addBalance(initialFromBalance);
		walletRepository.save(fromWallet);

		Wallet toWallet = new Wallet(toEmail, toPassword);
		toWallet.addBalance(initialToBalance);
		walletRepository.save(toWallet);

		Transfer transfer = transferUseCase.execute(fromEmail, toEmail, transferAmount);

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
		Email fromEmail = new Email("from@example.com");
		Email toEmail = new Email("to@example.com");
		Password fromPassword = new Password("password123");
		Password toPassword = new Password("password456");
		Money initialFromBalance = Money.of(100);
		Money initialToBalance = Money.of(50);
		Money transferAmount = Money.of(200);

		Wallet fromWallet = new Wallet(fromEmail, fromPassword);
		fromWallet.addBalance(initialFromBalance);
		walletRepository.save(fromWallet);

		Wallet toWallet = new Wallet(toEmail, toPassword);
		toWallet.addBalance(initialToBalance);
		walletRepository.save(toWallet);

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
		Email toEmail = new Email("to@example.com");
		Password toPassword = new Password("password456");
		Money transferAmount = Money.of(100);

		Wallet toWallet = new Wallet(toEmail, toPassword);
		toWallet.addBalance(Money.of(50));
		walletRepository.save(toWallet);

		assertThrows(WalletNotFoundException.class,
				() -> transferUseCase.execute(nonExistentEmail, toEmail, transferAmount));
	}

	@Test
	void throwsExceptionWhenToWalletDoesNotExist_004() {
		Email fromEmail = new Email("from@example.com");
		Email nonExistentEmail = new Email("nonexistent@example.com");
		Password fromPassword = new Password("password123");
		Money transferAmount = Money.of(100);

		Wallet fromWallet = new Wallet(fromEmail, fromPassword);
		fromWallet.addBalance(Money.of(200));
		walletRepository.save(fromWallet);

		assertThrows(WalletNotFoundException.class,
				() -> transferUseCase.execute(fromEmail, nonExistentEmail, transferAmount));

		Wallet unchangedFromWallet = walletRepository.findByEmail(fromEmail).orElseThrow();
		assertEquals(Money.of(200), unchangedFromWallet.getBalance());
	}
}