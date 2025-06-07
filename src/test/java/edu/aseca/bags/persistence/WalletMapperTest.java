package edu.aseca.bags.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.persistence.entity.WalletEntity;
import edu.aseca.bags.persistence.mapper.WalletMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class WalletMapperTest {

	@Autowired
	private WalletMapper walletMapper;

	@Test
	void toEntityShouldMapDomainToEntityCorrectly() {
		Email email = new Email("mapper@example.com");
		Password password = new Password("hashedpass");
		Wallet wallet = new Wallet(email, password);
		wallet.addBalance(new Money(150.0));

		WalletEntity entity = walletMapper.toEntity(wallet);

		assertEquals("mapper@example.com", entity.getEmail().address());
		assertEquals("hashedpass", entity.getPassword());
		assertEquals(BigDecimal.valueOf(150.0), entity.getBalance());
	}

	@Test
	void toDomainShouldMapEntityToDomainCorrectly() {
		WalletEntity entity = new WalletEntity("domain@example.com", "hashedPassword123", BigDecimal.valueOf(200.0));

		Wallet wallet = walletMapper.toDomain(entity);

		assertEquals("domain@example.com", wallet.getEmail().address());
		assertEquals("hashedPassword123", wallet.getPassword().hash());
		assertEquals(200.0, wallet.getBalance().amount());
	}

	@Test
	void toDomainWithZeroBalance() {
		WalletEntity entity = new WalletEntity("zero@example.com", "hash", BigDecimal.ZERO);

		Wallet wallet = walletMapper.toDomain(entity);

		assertEquals("zero@example.com", wallet.getEmail().address());
		assertEquals(0.0, wallet.getBalance().amount());
	}

}
