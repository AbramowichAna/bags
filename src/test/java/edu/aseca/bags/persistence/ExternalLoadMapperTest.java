package edu.aseca.bags.persistence;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.persistence.entity.ExternalLoadEntity;
import edu.aseca.bags.persistence.entity.WalletEntity;
import edu.aseca.bags.persistence.mapper.ExternalLoadMapper;
import edu.aseca.bags.testutil.TestExternalAccountFactory;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ExternalLoadMapperTest {

	@Test
	void toEntityShouldMapDomainToEntity_001() {
		UUID txId = UUID.randomUUID();
		Instant now = Instant.now();
		Wallet wallet = new Wallet(new Email("test@example.com"), new Password("pass"));
		Money amount = new Money(123.45);

		String serviceName = "BANK_TRANSFER";
		ServiceType serviceType = ServiceType.BANK;
		String serviceEmail = "bank@bank.com";
		ExternalAccount externalAccount = TestExternalAccountFactory.createExternalAccount(serviceName, serviceType,
				serviceEmail);

		WalletEntity walletEntity = new WalletEntity("test@example.com", "hash", BigDecimal.ZERO);

		ExternalLoad domain = new ExternalLoad(txId, wallet, amount, now, externalAccount);

		ExternalLoadEntity entity = ExternalLoadMapper.toEntity(domain, walletEntity);

		assertEquals(txId, entity.getTransactionId());
		assertEquals(walletEntity, entity.getToWallet());
		assertEquals(BigDecimal.valueOf(123.45), entity.getAmount());
		assertEquals(now, entity.getTimestamp());
		assertEquals(serviceName, entity.getExternalServiceName());
		assertEquals(serviceType.name(), entity.getExternalServiceType());
		assertEquals(serviceEmail, entity.getExternalServiceEmail());
	}

	@Test
	void toDomainShouldMapEntityToDomain_002() {
		UUID txId = UUID.randomUUID();
		Instant now = Instant.now();
		BigDecimal amount = BigDecimal.valueOf(99.99);

		String serviceName = "BANK_TRANSFER";
		String serviceType = "BANK";
		String serviceEmail = "bank@bank.com";

		WalletEntity walletEntity = new WalletEntity("user@domain.com", "hash", BigDecimal.ZERO);
		Wallet wallet = new Wallet(new Email("user@domain.com"), new Password("pass"));

		ExternalLoadEntity entity = new ExternalLoadEntity(txId, walletEntity, amount, now, serviceName, serviceType,
				serviceEmail);

		ExternalLoad domain = ExternalLoadMapper.toDomain(entity, wallet);

		assertEquals(txId, domain.transactionId());
		assertEquals(wallet, domain.toWallet());
		assertEquals(99.99, domain.amount().amount());
		assertEquals(now, domain.timestamp());
		assertEquals(serviceName, domain.externalAccount().externalServiceName());
		assertEquals(ServiceType.BANK, domain.externalAccount().serviceType());
		assertEquals(serviceEmail, domain.externalAccount().email().address());
	}
}