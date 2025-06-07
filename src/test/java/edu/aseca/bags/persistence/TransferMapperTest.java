package edu.aseca.bags.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.persistence.entity.TransferEntity;
import edu.aseca.bags.persistence.entity.WalletEntity;
import edu.aseca.bags.persistence.mapper.TransferMapper;
import edu.aseca.bags.persistence.mapper.WalletMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class TransferMapperTest {

	@Autowired
	private TransferMapper transferMapper;
	@Autowired
	private WalletMapper walletMapper;

	@Test
	void toEntityShouldMapDomainToEntityCorrectly_001() {
		Email fromEmail = new Email("from@example.com");
		Password fromPassword = new Password("frompass");
		Wallet fromWallet = new Wallet(fromEmail, fromPassword);

		Email toEmail = new Email("to@example.com");
		Password toPassword = new Password("topass");
		Wallet toWallet = new Wallet(toEmail, toPassword);

		Money amount = new Money(100.0);
		Instant timestamp = Instant.now();
		UUID transferNumber = UUID.randomUUID();
		Transfer transfer = new Transfer(TransferNumber.of(transferNumber), fromWallet, toWallet, amount, timestamp);

		TransferEntity entity = transferMapper.toEntity(transfer, walletMapper.toEntity(fromWallet),
				walletMapper.toEntity(toWallet));

		assertEquals(transferNumber, entity.getTransferNumber());
		assertEquals("from@example.com", entity.getFromWallet().getEmail().address());
		assertEquals("frompass", entity.getFromWallet().getPassword());
		assertEquals("to@example.com", entity.getToWallet().getEmail().address());
		assertEquals("topass", entity.getToWallet().getPassword());
		assertEquals(100.0, entity.getAmount().doubleValue());
		assertEquals(timestamp, entity.getTimestamp());
	}

	@Test
	void toDomainShouldMapEntityToDomainCorrectly_002() {
		WalletEntity fromWalletEntity = new WalletEntity("source@example.com", "sourcepass", BigDecimal.valueOf(500.0));
		WalletEntity toWalletEntity = new WalletEntity("destination@example.com", "destpass",
				BigDecimal.valueOf(300.0));

		Instant timestamp = Instant.now();
		UUID transferNumber = UUID.randomUUID();
		TransferEntity transferEntity = new TransferEntity(transferNumber, fromWalletEntity, toWalletEntity,
				BigDecimal.valueOf(150.0), timestamp);

		Transfer transfer = transferMapper.toDomain(transferEntity);

		assertEquals(TransferNumber.of(transferNumber), transfer.transferNumber());
		assertEquals("source@example.com", transfer.fromWallet().getEmail().address());
		assertEquals("sourcepass", transfer.fromWallet().getPassword().hash());
		assertEquals(500.0, transfer.fromWallet().getBalance().amount());

		assertEquals("destination@example.com", transfer.toWallet().getEmail().address());
		assertEquals("destpass", transfer.toWallet().getPassword().hash());
		assertEquals(300.0, transfer.toWallet().getBalance().amount());

		assertEquals(150.0, transfer.amount().amount());
		assertEquals(timestamp, transfer.timestamp());
	}

	@Test
	void nullTransferShouldMapToNull_003() {
		assertNull(transferMapper.toEntity(null, null, null));
		assertNull(transferMapper.toDomain(null));
	}
}