package edu.aseca.bags.persistence.mapper;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.persistence.entity.WalletEntity;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper implements ParticipantEntityMapper<WalletEntity, Wallet> {

	@Override
	public WalletEntity toEntity(Wallet wallet) {
		return new WalletEntity(wallet.getEmail().address(), wallet.getPassword().hash(),
				BigDecimal.valueOf(wallet.getBalance().amount()));
	}

	@Override
	public Class<WalletEntity> getEntityClass() {
		return WalletEntity.class;
	}

	@Override
	public Wallet toDomain(WalletEntity entity) {
		Email email = entity.getEmail();
		Password password = new Password(entity.getPassword());
		Wallet wallet = new Wallet(email, password);
		wallet.addBalance(new Money(entity.getBalance().doubleValue()));
		return wallet;
	}
}
