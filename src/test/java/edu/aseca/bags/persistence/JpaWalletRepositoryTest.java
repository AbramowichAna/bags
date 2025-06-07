package edu.aseca.bags.persistence;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.persistence.entity.WalletEntity;
import edu.aseca.bags.persistence.repository.JpaWalletRepository;
import edu.aseca.bags.persistence.repository.SpringWalletJpaRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class JpaWalletRepositoryTest {

	@Autowired
	private JpaWalletRepository repository;
	@Autowired
	private SpringWalletJpaRepository springRepository;

	@Test
	void saveWalletPersistsDataCorrectly() {
		Email email = new Email("user@example.com");
		Password password = new Password("hashedpass123");
		Wallet wallet = new Wallet(email, password);
		wallet.addBalance(new Money(100.0));

		repository.save(wallet);

		Optional<WalletEntity> entity = springRepository.findByEmail("user@example.com");
		assertTrue(entity.isPresent());
		assertEquals("user@example.com", entity.get().getEmail().address());
		assertEquals("hashedpass123", entity.get().getPassword());
		assertEquals(BigDecimal.valueOf(100.0), entity.get().getBalance());
		assertNotNull(entity.get().getId());

	}

	@Test
	void existsByEmailReturnsFalseWhenWalletDoesNotExist() {
		boolean exists = repository.existsByEmail(new Email("nonexistent@example.com"));
		assertFalse(exists);
	}
}
