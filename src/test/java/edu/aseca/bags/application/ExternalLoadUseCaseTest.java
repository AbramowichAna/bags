package edu.aseca.bags.application;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.application.dto.ExternalLoadRequest;
import edu.aseca.bags.application.dto.MovementView;
import edu.aseca.bags.application.interfaces.MovementIdGenerator;
import edu.aseca.bags.application.util.*;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.MovementId;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExternalLoadUseCaseTest {

	private InMemoryWalletRepository walletRepository;
	private InMemoryMovementRepository movementRepository;

	private InMemoryExternalAccountRepository externalAccountRepository;
	private ExternalLoadUseCase externalLoadUseCase;
	private Wallet wallet;
	private MovementIdGenerator movementIdGenerator;
	private MovementId knownMovementId;

	@BeforeEach
	void setup() {
		externalAccountRepository = new InMemoryExternalAccountRepository();
		walletRepository = new InMemoryWalletRepository();
		movementRepository = new InMemoryMovementRepository();
		knownMovementId = MovementId.random();
		movementIdGenerator = new KnownMovementIdGenerator(knownMovementId);
		externalLoadUseCase = new ExternalLoadUseCase(walletRepository, movementRepository, externalAccountRepository,
				movementIdGenerator);
		wallet = new Wallet(new Email("user@gmail.com"), new Password("hola12345"));
		walletRepository.save(wallet);
	}

	private ExternalLoadRequest buildRequest(String email, BigDecimal amount) {
		return new ExternalLoadRequest(email, amount, "BANK_TRANSFER", "BANK", "bank@bank.com");
	}

	@Test
	public void shouldIncreaseWalletBalanceSuccessfully_001() throws WalletNotFoundException {
		ExternalLoadRequest request = buildRequest(wallet.getEmail().address(), BigDecimal.valueOf(100.0));
		MovementView response = externalLoadUseCase.loadFromExternal(request);
		assertEquals(100.0, wallet.getBalance().amount());
		assertEquals(knownMovementId.value().toString(), response.id());
		assertEquals(wallet.getEmail().address(), response.toParticipant().email());
		assertEquals("BANK_TRANSFER", response.fromParticipant().serviceName());
	}

	@Test
	public void shouldFailToLoadWithNegativeAmount_002() {
		ExternalLoadRequest request = buildRequest(wallet.getEmail().address(), BigDecimal.valueOf(-50.0));
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			externalLoadUseCase.loadFromExternal(request);
		});
		assertEquals("Amount must be greater than zero", ex.getMessage());
	}

	@Test
	public void shouldFailToLoadWithNonExistingWallet_003() {
		ExternalLoadRequest request = buildRequest("hola@gmail.com", BigDecimal.valueOf(100.0));
		assertThrows(WalletNotFoundException.class, () -> {
			externalLoadUseCase.loadFromExternal(request);
		});
	}

	@Test
	public void shouldFailToLoadWithNullAmount_004() {
		ExternalLoadRequest request = buildRequest(wallet.getEmail().address(), null);
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			externalLoadUseCase.loadFromExternal(request);
		});
		assertEquals("Amount must not be null", ex.getMessage());
	}

	@Test
	void shouldFailWhenMovementIdAlreadyExistsDuplicateTransactionId_005() {
		ExternalLoadRequest request = buildRequest(wallet.getEmail().address(), BigDecimal.valueOf(100.0));
		movementRepository.save(new edu.aseca.bags.domain.transaction.Movement(knownMovementId,
				new edu.aseca.bags.domain.participant.ExternalAccount("BANK_TRANSFER",
						edu.aseca.bags.domain.participant.ServiceType.BANK, new Email("bank@bank.com")),
				wallet, java.time.Instant.now(), new edu.aseca.bags.domain.money.Money(100.0),
				edu.aseca.bags.domain.transaction.MovementType.EXTERNAL_IN));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			externalLoadUseCase.loadFromExternal(request);
		});
		assertEquals("External reference already used", ex.getMessage());
	}

	@Test
	void shouldFailWithUnknownExternalServiceInvalidServiceData_006() {
		ExternalLoadRequest request = new ExternalLoadRequest(wallet.getEmail().address(), BigDecimal.valueOf(100.0),
				"UNKNOWN_SERVICE", "UNKNOWN_TYPE", "unknown@service.com");
		assertThrows(IllegalArgumentException.class, () -> {
			externalLoadUseCase.loadFromExternal(request);
		});
	}

}