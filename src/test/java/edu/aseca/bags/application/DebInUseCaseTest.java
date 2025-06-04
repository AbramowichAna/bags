package edu.aseca.bags.application;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.application.util.FakeExternalApiClient;
import edu.aseca.bags.application.util.InMemoryExternalLoadRepository;
import edu.aseca.bags.application.util.InMemoryWalletRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.exception.AlreadyLinkedExternalAccount;
import edu.aseca.bags.exception.UnsupportedExternalService;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.testutil.TestWalletFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DebInUseCaseTest {

	InMemoryWalletRepository walletRepository;
	InMemoryExternalLoadRepository externalLoadRepository;
	FakeExternalApiClient externalApiClient;
	DebInUseCase debInUseCase;
	Wallet wallet;

	@BeforeEach
	void setup() {
		walletRepository = new InMemoryWalletRepository();
		externalLoadRepository = new InMemoryExternalLoadRepository();
		externalApiClient = new FakeExternalApiClient();
		debInUseCase = new DebInUseCase(walletRepository, externalApiClient);
		String email = "user@gmail.com";
		wallet = TestWalletFactory.createAndSave(walletRepository, email, "hola12345", 0.0);
	}

	/*
	 * Test cases for linking external services and requesting loads from them.
	 * Cases: 1. Successful linking of an external service with valid credentials.
	 * 2. Unknown service name 3. Wrong credentials provided for the external
	 * service
	 */

	@Test
	void linkExternalAccountSuccessfulVerificationLinksAndSaves_001() {
		assertDoesNotThrow(() -> debInUseCase.linkExternalService(getSomeWalletEmail(), "Bank", ServiceType.BANK,
				getSomeMail(), getSomePassword()));
		assertTrue(wallet.getExternalAccounts().stream().anyMatch(a -> a.externalServiceName().equals("Bank")));
	}

	@Test
	public void linkExternalAccountFailsDueToUnknownServiceName_002() {
		assertThrows(UnsupportedExternalService.class, () -> debInUseCase.linkExternalService(getSomeWalletEmail(),
				"UnknownService", ServiceType.BANK, getSomeMail(), getSomePassword()));
	}

	@Test
	void linkExternalAccountFailedVerificationFails_003() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> debInUseCase
				.linkExternalService(getSomeWalletEmail(), "Bank", ServiceType.BANK, getSomeMail(), "badpass"));
		assertEquals("External account credentials are invalid", ex.getMessage());
	}

	@Test
	void alreadyLinkedExternalAccountFails_004() {
		assertDoesNotThrow(() -> debInUseCase.linkExternalService(getSomeWalletEmail(), "Bank", ServiceType.BANK,
				getSomeMail(), getSomePassword()));
		AlreadyLinkedExternalAccount ex = assertThrows(AlreadyLinkedExternalAccount.class, () -> debInUseCase
				.linkExternalService(getSomeWalletEmail(), "Bank", ServiceType.BANK, getSomeMail(), getSomePassword()));
		assertEquals("External account already linked", ex.getMessage());
	}

	@Test
	void linkExternalServiceFailsWhenWalletNotFound_005() {
		Email nonExistent = new Email("noone@nowhere.com");
		assertThrows(WalletNotFoundException.class, () -> debInUseCase.linkExternalService(nonExistent, "Bank",
				ServiceType.BANK, getSomeMail(), getSomePassword()));
	}

	@Test
	void linkMultipleExternalAccounts_006() {
		assertDoesNotThrow(() -> debInUseCase.linkExternalService(getSomeWalletEmail(), "Bank", ServiceType.BANK,
				getSomeMail(), getSomePassword()));
		assertDoesNotThrow(() -> debInUseCase.linkExternalService(getSomeWalletEmail(), "PayPal",
				ServiceType.VIRTUAL_WALLET, "user@paypal.com", getSomePassword()));
		assertEquals(2, wallet.getExternalAccounts().size());
	}

	/*
	 * Test cases for requesting loads from external services. Cases: 1. Successful
	 * load request from a linked external service. 2. Does not recognize the
	 * external service name. 3. External service does not have the account 4.
	 * Should fail if the account is not linked
	 */

	@Test
	void requestLoadFromExternalServiceSuccessful_007() {
		assertDoesNotThrow(() -> debInUseCase.linkExternalService(getSomeWalletEmail(), "Bank", ServiceType.BANK,
				getSomeMail(), getSomePassword()));
		assertDoesNotThrow(() -> debInUseCase.requestLoadFromExternalService(getSomeWalletEmail(), "Bank",
				ServiceType.BANK, getSomeMail(), 100.0));

		ExternalAccount externalAccount = wallet.getExternalAccounts().stream()
				.filter(a -> a.externalServiceName().equals("Bank")).findFirst().get();
		assertTrue(externalApiClient.receivedRequest(externalAccount));
	}

	@Test
	void requestLoadFromExternalServiceFailsDueToUnknownServiceName_008() {
		UnsupportedExternalService ex = assertThrows(UnsupportedExternalService.class,
				() -> debInUseCase.requestLoadFromExternalService(getSomeWalletEmail(), "UnknownService",
						ServiceType.BANK, getSomeMail(), 100.0));
		assertEquals("Unsupported external service: UnknownService", ex.getMessage());
	}

	@Test
	void shouldFailIfAccountIsNotLinked_009() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> debInUseCase
				.requestLoadFromExternalService(getSomeWalletEmail(), "Bank", ServiceType.BANK, getSomeMail(), 100.0));
		assertEquals("Wallet is not linked to external account", ex.getMessage());
	}

	@Test
	void requestLoadFailsWhenWalletNotFound_010() {
		Email nonExistent = new Email("noone@nowhere.com");
		assertThrows(WalletNotFoundException.class, () -> debInUseCase.requestLoadFromExternalService(nonExistent,
				"Bank", ServiceType.BANK, getSomeMail(), 100.0));
	}

	@Test
	void requestLoadFailsWithNegativeAmount_011() throws WalletNotFoundException, UnsupportedExternalService {
		debInUseCase.linkExternalService(getSomeWalletEmail(), "Bank", ServiceType.BANK, getSomeMail(),
				getSomePassword());
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> debInUseCase
				.requestLoadFromExternalService(getSomeWalletEmail(), "Bank", ServiceType.BANK, getSomeMail(), -50.0));
		assertEquals("Amount cannot be negative: -50.0", ex.getMessage());
	}

	private static @NotNull String getSomeMail() {
		return "user@bank.com";
	}

	private static @NotNull String getSomePassword() {
		return "goodpass";
	}

	private static @NotNull Email getSomeWalletEmail() {
		return new Email("user@gmail.com");
	}
}