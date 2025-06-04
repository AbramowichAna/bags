package edu.aseca.bags.persistence;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Wallet;
import java.math.BigDecimal;

public class WalletMapper {

	public static WalletEntity toEntity(Wallet wallet) {
		return new WalletEntity(wallet.getEmail().address(), wallet.getPassword().hash(),
				BigDecimal.valueOf(wallet.getBalance().amount()));
	}

	public static Wallet toDomain(WalletEntity entity) {
		Email email = new Email(entity.getEmail());
		Password password = new Password(entity.getPassword());
		Wallet wallet = new Wallet(email, password);
		wallet.addBalance(new Money(entity.getBalance().doubleValue()));
		return wallet;
	}
}
