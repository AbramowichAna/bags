package edu.aseca.bags.persistence;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.persistence.entity.ExternalLoadEntity;
import edu.aseca.bags.persistence.entity.WalletEntity;
import edu.aseca.bags.persistence.mapper.WalletMapper;
import edu.aseca.bags.persistence.repository.JpaExternalLoadRepository;
import edu.aseca.bags.persistence.repository.SpringExternalLoadJpaRepository;
import edu.aseca.bags.persistence.repository.SpringWalletJpaRepository;
import edu.aseca.bags.testutil.TestExternalAccountFactory;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JpaExternalLoadRepositoryTest {

	@Autowired
	private SpringExternalLoadJpaRepository externalLoadJpaRepository;

	@Autowired
	private SpringWalletJpaRepository walletJpaRepository;
	@Autowired
	private WalletMapper walletMapper;

	private JpaExternalLoadRepository jpaExternalLoadRepository;

	@BeforeEach
	void setUp() {
		jpaExternalLoadRepository = new JpaExternalLoadRepository(externalLoadJpaRepository, walletJpaRepository,
				walletMapper);
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

		String serviceName = "BANK_TRANSFER";
		ServiceType serviceType = ServiceType.BANK;
		String serviceEmail = "bank@bank.com";
		ExternalAccount externalAccount = TestExternalAccountFactory.createExternalAccount(serviceName, serviceType,
				serviceEmail);

		ExternalLoad externalLoad = new ExternalLoad(txId, wallet, amount, now, externalAccount);

		jpaExternalLoadRepository.save(externalLoad);

		Optional<ExternalLoadEntity> found = externalLoadJpaRepository.findAll().stream()
				.filter(e -> e.getTransactionId().equals(txId)).findFirst();

		assertTrue(found.isPresent());
		ExternalLoadEntity entity = found.get();
		assertEquals(txId, entity.getTransactionId());
		assertEquals(BigDecimal.valueOf(50.0), entity.getAmount());
		assertEquals(serviceName, entity.getExternalServiceName());
		assertEquals(serviceType.name(), entity.getExternalServiceType());
		assertEquals(serviceEmail, entity.getExternalServiceEmail());
		assertEquals(email, entity.getToWallet().getEmail().address());
	}
}