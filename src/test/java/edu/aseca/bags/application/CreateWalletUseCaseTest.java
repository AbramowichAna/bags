package edu.aseca.bags.application;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.exception.AlreadyExistingWallet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateWalletUseCaseTest {

	InMemoryWalletRepository repo;
	StubPasswordEncoder encoder;
	CreateWalletUseCase useCase;

	@BeforeEach
	void setup() {
		repo = new InMemoryWalletRepository();
		encoder = new StubPasswordEncoder();
		useCase = new CreateWalletUseCase(repo, encoder);
	}

	@Test
	void createsWalletSuccessfully_001() {
		assertDoesNotThrow(() -> useCase.create("user@example.com", "mypassword"));

		Optional<Wallet> result = repo.findByEmail(new Email("user@example.com"));
		assertTrue(result.isPresent());
		assertEquals("ENC(mypassword)", result.get().getPassword().hash());
	}

	@Test
	void throwsIfWalletExists_002() {
		Email email = new Email("taken@example.com");
		Wallet existing = new Wallet(email, new Password("ENC(secret)"));
		repo.save(existing);

		AlreadyExistingWallet ex = assertThrows(AlreadyExistingWallet.class,
				() -> useCase.create("taken@example.com", "anotherpass"));

		assertEquals(AlreadyExistingWallet.MESSAGE, ex.getMessage());
	}

	@Test
	void throwsOnInvalidEmail_003() {
		assertThrows(IllegalArgumentException.class, () -> useCase.create("not-an-email", "password123"));
	}

	@Test
	void throwsOnBlankPassword_004() {
		assertThrows(IllegalArgumentException.class, () -> useCase.create("valid@example.com", "   "));
	}

	@Test
	void acceptsEdgeCaseEmails_005() {
		String email = "user.name+tag@sub.domain.com";
		assertDoesNotThrow(() -> useCase.create(email, "edgepass"));
		Optional<Wallet> wallet = repo.findByEmail(new Email(email));
		assertTrue(wallet.isPresent());
	}

	@Test
	void acceptsPasswordWithSpecialCharacters_006() {
		assertDoesNotThrow(() -> useCase.create("char@example.com", "p@$$w0rd!_123"));
		Wallet wallet = repo.findByEmail(new Email("char@example.com")).orElseThrow();
		assertEquals("ENC(p@$$w0rd!_123)", wallet.getPassword().hash());
	}
}