package edu.aseca.bags.persistence.mapper;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.persistence.entity.ExternalLoadEntity;
import edu.aseca.bags.persistence.entity.WalletEntity;
import java.math.BigDecimal;

public class ExternalLoadMapper {

	public static ExternalLoadEntity toEntity(ExternalLoad externalLoad, WalletEntity toWalletEntity) {
		return new ExternalLoadEntity(externalLoad.transactionId(), toWalletEntity,
				BigDecimal.valueOf(externalLoad.amount().amount()), externalLoad.timestamp(),
				externalLoad.externalAccount().externalServiceName(),
				externalLoad.externalAccount().serviceType().name(), externalLoad.externalAccount().email().address());
	}

	public static ExternalLoad toDomain(ExternalLoadEntity entity, Wallet toWallet) {
		ExternalAccount externalAccount = new ExternalAccount(entity.getExternalServiceName(),
				ServiceType.valueOf(entity.getExternalServiceType()), new Email(entity.getExternalServiceEmail()));
		return new ExternalLoad(entity.getTransactionId(), toWallet, new Money(entity.getAmount().doubleValue()),
				entity.getTimestamp(), externalAccount);
	}
}