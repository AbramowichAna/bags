
package edu.aseca.bags.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.wallet.Wallet;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

public class TransferMapperTest {

	@Test
	void toEntityShouldMapDomainToEntityCorrectly() {
		Email fromEmail = new Email("from@example.com");
		Password fromPassword = new Password("frompass");
		Wallet fromWallet = new Wallet(fromEmail, fromPassword);

		Email toEmail = new Email("to@example.com");
		Password toPassword = new Password("topass");
		Wallet toWallet = new Wallet(toEmail, toPassword);

		Money amount = new Money(100.0);
		Instant timestamp = Instant.now();
		Transfer transfer = new Transfer(fromWallet, toWallet, amount, timestamp);

		TransferEntity entity = TransferMapper.toEntity(transfer);

		assertEquals("from@example.com", entity.getFromWallet().getEmail());
		assertEquals("frompass", entity.getFromWallet().getPassword());
		assertEquals("to@example.com", entity.getToWallet().getEmail());
		assertEquals("topass", entity.getToWallet().getPassword());
		assertEquals(100.0, entity.getAmount());
		assertEquals(timestamp, entity.getTimestamp());
	}

	@Test
	void toDomainShouldMapEntityToDomainCorrectly() {
		WalletEntity fromWalletEntity = new WalletEntity("source@example.com", "sourcepass", BigDecimal.valueOf(500.0));
		WalletEntity toWalletEntity = new WalletEntity("destination@example.com", "destpass",
				BigDecimal.valueOf(300.0));

		Instant timestamp = Instant.now();
		TransferEntity transferEntity = new TransferEntity(fromWalletEntity, toWalletEntity, 150.0, timestamp);

		Transfer transfer = TransferMapper.toDomain(transferEntity);

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
	void nullTransferShouldMapToNull() {
		assertNull(TransferMapper.toEntity(null));
		assertNull(TransferMapper.toDomain(null));
	}
}