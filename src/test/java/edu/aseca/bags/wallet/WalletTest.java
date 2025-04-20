package edu.aseca.bags.wallet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.aseca.bags.email.Email;
import edu.aseca.bags.email.Password;
import edu.aseca.bags.money.Money;
import org.junit.jupiter.api.Test;

/*
* Que exista
*   No se puede con mails duplicados
* Mostrar plata
*   Una cuenta nueva arranca con 0 pesos
*   Debe mostrar el balance correcto despues de agregar balance
*   Debe mostrar el balance correcto despues de sacar balance
* Ver transacciones pasadas
* Consultar mi información (email)
*/

class WalletTest {

	private final Email validEmail = new Email("valid@email.com");
	private final Password validPassword = new Password("password");

	@Test
	public void shouldCreateNewAccountWithEmailAddressAndPassword_001() {
		assertDoesNotThrow(() -> new Wallet(validEmail, validPassword));
	}

	@Test
	public void shouldReturn0ForNewAccount_002() {
		Wallet wallet = new Wallet(validEmail, validPassword);
		assertEquals(Money.of(0), wallet.getBalance());
	}

	@Test
	public void shouldReturnTheCorrectBalanceAfterAddingBalance_003() {
		Wallet wallet = new Wallet(validEmail, validPassword);
		Money initialAmount = new Money(3);

		assertEquals(Money.of(0), wallet.getBalance(), "Initial balance should be zero");

		wallet.addBalance(initialAmount);
		assertEquals(initialAmount, wallet.getBalance(), "Balance after first addition is incorrect");

		wallet.addBalance(initialAmount);

		Money expectedBalanceAfterSecondAdd = initialAmount.add(initialAmount);
		assertEquals(expectedBalanceAfterSecondAdd, wallet.getBalance(), "Balance after second addition is incorrect");
	}

	@Test
	public void shouldReturnTheCorrectBalanceAfterSubtractingBalance_004() {
		Wallet wallet = new Wallet(validEmail, validPassword);

		Money initialAmount = new Money(10);
		wallet.addBalance(initialAmount);

		Money subtractedAmount = new Money(4);
		Money expectedBalance = initialAmount.subtract(subtractedAmount);

		wallet.subtractBalance(subtractedAmount);
		assertEquals(expectedBalance, wallet.getBalance(), "Balance after subtraction is incorrect");
	}

	@Test
	public void whenSubtractingFromEmptyWalletShouldThrowException_005() {
		Wallet wallet = new Wallet(validEmail, validPassword);

		assertEquals(Money.of(0), wallet.getBalance(), "Balance should be zero");
		assertThrows(IllegalStateException.class, () -> wallet.subtractBalance(Money.of(10)));
	}

	@Test
	public void whenSubtractingWithoutEnoughFundsShouldThrowException_006() {
		Wallet wallet = new Wallet(validEmail, validPassword);

		wallet.addBalance(Money.of(10));
		assertEquals(Money.of(10), wallet.getBalance(), "Balance should be ten");
		assertThrows(IllegalStateException.class, () -> wallet.subtractBalance(Money.of(20)));
	}

}