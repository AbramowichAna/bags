package edu.aseca.bags.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Wallet;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class WalletMapperTest {
	@Test
	void toEntityShouldMapDomainToEntityCorrectly() {
		Email email = new Email("mapper@example.com");
		Password password = new Password("hashedpass");
		Wallet wallet = new Wallet(email, password);
		wallet.addBalance(new Money(150.0));

		WalletEntity entity = WalletMapper.toEntity(wallet);

		assertEquals("mapper@example.com", entity.getEmail());
		assertEquals("hashedpass", entity.getPassword());
		assertEquals(BigDecimal.valueOf(150.0), entity.getBalance());
	}

	@Test
	void toDomainShouldMapEntityToDomainCorrectly() {
		WalletEntity entity = new WalletEntity("domain@example.com", "hashedPassword123", BigDecimal.valueOf(200.0));

		Wallet wallet = WalletMapper.toDomain(entity);

		assertEquals("domain@example.com", wallet.getEmail().address());
		assertEquals("hashedPassword123", wallet.getPassword().hash());
		assertEquals(200.0, wallet.getBalance().amount());
	}

	@Test
	void toDomainWithZeroBalance() {
		WalletEntity entity = new WalletEntity("zero@example.com", "hash", BigDecimal.ZERO);

		Wallet wallet = WalletMapper.toDomain(entity);

		assertEquals("zero@example.com", wallet.getEmail().address());
		assertEquals(0.0, wallet.getBalance().amount());
	}

}
