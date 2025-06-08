package edu.aseca.bags.application;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.application.util.FakeExternalApiClient;
import edu.aseca.bags.application.util.InMemoryWalletRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.exception.UnsupportedExternalService;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.testutil.TestWalletFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DebInUseCaseTest {

	InMemoryWalletRepository walletRepository;
	FakeExternalApiClient externalApiClient;
	DebInUseCase debInUseCase;
	Wallet wallet;

	@BeforeEach
	void setup() {
		walletRepository = new InMemoryWalletRepository();
		externalApiClient = new FakeExternalApiClient();
		debInUseCase = new DebInUseCase(walletRepository, externalApiClient);
		wallet = TestWalletFactory.createAndSave(walletRepository, "user@gmail.com", "hola12345", 0.0);
	}

	@Test
	void successfulLoadRequest_001() throws Exception {
		String externalServiceName = "Bank";
		ServiceType type = ServiceType.BANK;
		String externalEmail = "external@bank.com";
		double amount = 100.0;

		debInUseCase.requestDebIn(wallet.getEmail(), externalServiceName, type, externalEmail, amount);

		ExternalAccount expected = new ExternalAccount(externalServiceName, type, new Email(externalEmail));
		assertTrue(externalApiClient.receivedRequest(expected));
	}

	@Test
	void throwsWhenServiceNotSupported_002() {
		String externalServiceName = "UnknownService";
		ServiceType type = ServiceType.BANK;
		String externalEmail = "external@bank.com";
		double amount = 100.0;

		assertThrows(UnsupportedExternalService.class,
				() -> debInUseCase.requestDebIn(wallet.getEmail(), externalServiceName, type, externalEmail, amount));
	}

	@Test
	void throwsWhenWalletNotFound_003() {
		String externalServiceName = "Bank";
		ServiceType type = ServiceType.BANK;
		String externalEmail = "external@bank.com";
		double amount = 100.0;
		Email nonExistentWallet = new Email("notfound@gmail.com");

		assertThrows(WalletNotFoundException.class,
				() -> debInUseCase.requestDebIn(nonExistentWallet, externalServiceName, type, externalEmail, amount));
	}

	@Test
	void throwsWhenAmountIsZero_004() {
		String externalServiceName = "Bank";
		ServiceType type = ServiceType.BANK;
		String externalEmail = "external@bank.com";
		double amount = 0.0;

		assertThrows(IllegalArgumentException.class,
				() -> debInUseCase.requestDebIn(wallet.getEmail(), externalServiceName, type, externalEmail, amount));
	}

	@Test
	void throwsWhenAmountIsNegative_005() {
		String externalServiceName = "Bank";
		ServiceType type = ServiceType.BANK;
		String externalEmail = "external@bank.com";
		double amount = -50.0;

		assertThrows(IllegalArgumentException.class,
				() -> debInUseCase.requestDebIn(wallet.getEmail(), externalServiceName, type, externalEmail, amount));
	}

	@Test
	void throwsWhenExternalEmailIsNull_006() {
		String externalServiceName = "Bank";
		ServiceType type = ServiceType.BANK;
		String externalEmail = null;
		double amount = 100.0;

		assertThrows(IllegalArgumentException.class,
				() -> debInUseCase.requestDebIn(wallet.getEmail(), externalServiceName, type, externalEmail, amount));
	}

	@Test
	void throwsWhenWalletEmailIsNull_007() {
		String externalServiceName = "Bank";
		ServiceType type = ServiceType.BANK;
		String externalEmail = "external@bank.com";
		double amount = 100.0;

		assertThrows(IllegalArgumentException.class,
				() -> debInUseCase.requestDebIn(null, externalServiceName, type, externalEmail, amount));
	}
}