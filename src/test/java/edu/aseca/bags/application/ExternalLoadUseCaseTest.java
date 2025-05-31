package edu.aseca.bags.application;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.api.dto.ExternalLoadRequest;
import edu.aseca.bags.api.dto.ExternalLoadResponse;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExternalLoadUseCaseTest {

	InMemoryWalletRepository walletRepository;
	InMemoryExternalLoadRepository externalLoadRepository;
	ExternalLoadUseCase externalLoadUseCase;
	Wallet wallet;

	@BeforeEach
	void setup() {
		walletRepository = new InMemoryWalletRepository();
		externalLoadRepository = new InMemoryExternalLoadRepository();
		externalLoadUseCase = new ExternalLoadUseCase(walletRepository, externalLoadRepository);
		wallet = new Wallet(new Email("user@gmail.com"), new Password("hola12345"));
		walletRepository.save(wallet);
	}

	private ExternalLoadRequest buildRequest(String email, BigDecimal amount, String txId) {
		ExternalLoadRequest req = new ExternalLoadRequest();
		try {
			var f1 = req.getClass().getDeclaredField("walletEmail");
			f1.setAccessible(true);
			f1.set(req, email);
			var f2 = req.getClass().getDeclaredField("amount");
			f2.setAccessible(true);
			f2.set(req, amount);
			var f3 = req.getClass().getDeclaredField("externalService");
			f3.setAccessible(true);
			f3.set(req, "BANK_TRANSFER");
			var f4 = req.getClass().getDeclaredField("externalTransactionId");
			f4.setAccessible(true);
			f4.set(req, txId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return req;
	}

	@Test
	public void shouldIncreaseWalletBalanceSuccessfully_001() throws WalletNotFoundException {
		String txId = UUID.randomUUID().toString();
		ExternalLoadRequest request = buildRequest(wallet.getEmail().address(), BigDecimal.valueOf(100.0), txId);
		ExternalLoadResponse response = externalLoadUseCase.loadFromExternal(request);
		assertEquals(100.0, wallet.getBalance().amount());
		assertEquals("SUCCESS", response.getStatus());
		assertEquals(BigDecimal.valueOf(100.0), response.getAmount());
	}

	@Test
	public void shouldFailToLoadWithNegativeAmount_002() {
		String txId = UUID.randomUUID().toString();
		ExternalLoadRequest request = buildRequest(wallet.getEmail().address(), BigDecimal.valueOf(-50.0), txId);
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			externalLoadUseCase.loadFromExternal(request);
		});
		assertEquals("Amount must be greater than zero", ex.getMessage());
	}

	@Test
	public void shouldFailToLoadWithNonExistingWallet_003() {
		String txId = UUID.randomUUID().toString();
		ExternalLoadRequest request = buildRequest("hola@gmail.com", BigDecimal.valueOf(100.0), txId);
		assertThrows(WalletNotFoundException.class, () -> {
			externalLoadUseCase.loadFromExternal(request);
		});
	}

	@Test
	public void shouldFailToLoadWithNullAmount_004() {
		String txId = UUID.randomUUID().toString();
		ExternalLoadRequest request = buildRequest(wallet.getEmail().address(), null, txId);
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			externalLoadUseCase.loadFromExternal(request);
		});
		assertEquals("Amount and transfer method must not be null", ex.getMessage());
	}
}