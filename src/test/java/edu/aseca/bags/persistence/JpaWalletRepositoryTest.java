package edu.aseca.bags.persistence;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Wallet;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class JpaWalletRepositoryTest {

	@Autowired
	private SpringWalletJpaRepository springRepository;

	@Test
	void saveWalletPersistsDataCorrectly() {
		JpaWalletRepository repository = new JpaWalletRepository(springRepository);
		Email email = new Email("user@example.com");
		Password password = new Password("hashedpass123");
		Wallet wallet = new Wallet(email, password);
		wallet.addBalance(new Money(100.0));

		repository.save(wallet);

		Optional<WalletEntity> entity = springRepository.findByEmail("user@example.com");
		assertTrue(entity.isPresent());
		assertEquals("user@example.com", entity.get().getEmail());
		assertEquals("hashedpass123", entity.get().getPassword());
		assertEquals(BigDecimal.valueOf(100.0), entity.get().getBalance());
		assertNotNull(entity.get().getId());

	}

	@Test
	void existsByEmailReturnsFalseWhenWalletDoesNotExist() {
		JpaWalletRepository repository = new JpaWalletRepository(springRepository);
		boolean exists = repository.existsByEmail(new Email("nonexistent@example.com"));
		assertFalse(exists);
	}
}
