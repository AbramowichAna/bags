package edu.aseca.bags.persistence;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.domain.transaction.ExternalService;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class JpaExternalLoadRepositoryTest {

	@Autowired
	private SpringExternalLoadJpaRepository externalLoadJpaRepository;

	@Autowired
	private SpringWalletJpaRepository walletJpaRepository;

	private JpaExternalLoadRepository jpaExternalLoadRepository;

	@BeforeEach
	void setUp() {
		jpaExternalLoadRepository = new JpaExternalLoadRepository(externalLoadJpaRepository, walletJpaRepository);
		walletJpaRepository.deleteAll();
		externalLoadJpaRepository.deleteAll();
	}

	@Test
	void saveShouldPersistExternalLoad_001() throws WalletNotFoundException {
		String email = "test@domain.com";
		WalletEntity walletEntity = new WalletEntity(email, "hash", BigDecimal.ZERO);
		walletJpaRepository.save(walletEntity);

		Wallet wallet = new Wallet(new Email(email), new Password("pass"));
		UUID txId = UUID.randomUUID();
		Money amount = new Money(50.0);
		Instant now = Instant.now();
		ExternalService service = ExternalService.BANK_TRANSFER;

		ExternalLoad externalLoad = new ExternalLoad(txId, wallet, amount, now, service);

		jpaExternalLoadRepository.save(externalLoad);

		Optional<ExternalLoadEntity> found = externalLoadJpaRepository.findAll().stream()
				.filter(e -> e.getTransactionId().equals(txId)).findFirst();

		assertTrue(found.isPresent());
		ExternalLoadEntity entity = found.get();
		assertEquals(txId, entity.getTransactionId());
		assertEquals(BigDecimal.valueOf(50.0), entity.getAmount());
		assertEquals(service.name(), entity.getService());
		assertEquals(email, entity.getToWallet().getEmail());
	}
}