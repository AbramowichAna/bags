package edu.aseca.bags.application;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.application.dto.WalletInfo;
import edu.aseca.bags.exception.BadPermissionException;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.testutil.TestWalletFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletQueryTest {

	private InMemoryWalletRepository repo;
	private WalletQuery query;

	private final String email = "gregorsamsa@gmail.com";

	@BeforeEach
	void setup() {
		repo = new InMemoryWalletRepository();
		query = new WalletQuery(repo);
		repo.save(TestWalletFactory.createWallet(email, "password"));
	}

	@Test
	public void shouldReturnInfoOfAccountWithZeroBalance_001() {
		WalletInfo info = assertDoesNotThrow(() -> query.getWalletInfoOf(email, email));
		assertEquals(0, info.balance().amount());
	}

	@Test
	public void shouldReturnInfoOfAccountWithPositiveBalance_002() {
		repo.save(TestWalletFactory.createWallet(email, "password", 100));
		WalletInfo info = assertDoesNotThrow(() -> query.getWalletInfoOf(email, email));
		assertEquals(100, info.balance().amount());
	}

	@Test
	public void shouldFailWhenAccountDoesNotExist_003() {
		String mail = "notExistent@gmail.com";
		assertThrows(WalletNotFoundException.class, () -> query.getWalletInfoOf(mail, mail));
	}

	@Test
	public void shouldFailWithMalformedEmail_003() {
		String walletOwnerEmail = "notExistentgmail.com";
		assertThrows(IllegalArgumentException.class, () -> query.getWalletInfoOf(walletOwnerEmail, walletOwnerEmail));
	}

	@Test
	public void shouldFailWhenAttemptingToAccessAnotherMailAccount_003() {
		String requester = "hacker@gmail.com";
		assertThrows(BadPermissionException.class, () -> query.getWalletInfoOf(email, requester));
	}
}