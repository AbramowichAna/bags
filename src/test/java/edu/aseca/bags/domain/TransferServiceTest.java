package edu.aseca.bags.domain;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.InsufficientFundsException;
import edu.aseca.bags.domain.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/* Casos
    - Cuenta base tiene dinero y se lo puede dar al otro
    - Cuenta base no tiene fondos y falla la transacción
    - No existe from o to
 */

@SpringBootTest
@ActiveProfiles("test")
public class TransferServiceTest {

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private TransferService transferService;

	@BeforeEach
	void setup() {
		walletRepository.deleteAll();
	}

	@Test
	public void transferSucceeds_001() {
		Wallet from = saveWallet("from@email.com", 500);
		Wallet to = saveWallet("to@email.com", 0);

		assertDoesNotThrow(() -> transferService.transfer(from.getEmail(), to.getEmail(), Money.of(100)));

		Wallet updatedFrom = walletRepository.findByEmail(from.getEmail()).orElseThrow();
		Wallet updatedTo = walletRepository.findByEmail(to.getEmail()).orElseThrow();

		assertEquals(Money.of(400), updatedFrom.getBalance());
		assertEquals(Money.of(100), updatedTo.getBalance());
	}

	@Test
	public void transferFailsBecauseFromDoesNotHaveFunds_002() {
		Wallet from = saveWallet("from@email.com", 0);
		Wallet to = saveWallet("to@email.com", 0);
		assertThrows(InsufficientFundsException.class,
				() -> transferService.transfer(from.getEmail(), to.getEmail(), Money.of(100)));
	}

	private Wallet saveWallet(String email, int balance) {
		Wallet wallet = new Wallet(new Email(email), new Password("password"));
		wallet.addBalance(Money.of(balance));
		return walletRepository.save(wallet);
	}

}
