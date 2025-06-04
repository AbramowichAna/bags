package edu.aseca.bags.persistence;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import java.math.BigDecimal;

public class ExternalLoadMapper {

	public static ExternalLoadEntity toEntity(ExternalLoad externalLoad, WalletEntity toWalletEntity) {
		return new ExternalLoadEntity(externalLoad.transactionId(), toWalletEntity,
				BigDecimal.valueOf(externalLoad.amount().amount()), externalLoad.timestamp(),
				externalLoad.externalAccount().externalServiceName(),
				externalLoad.externalAccount().serviceType().name(), externalLoad.externalAccount().email());
	}

	public static ExternalLoad toDomain(ExternalLoadEntity entity, Wallet toWallet) {
		ExternalAccount externalAccount = new ExternalAccount(entity.getExternalServiceName(),
				ServiceType.valueOf(entity.getExternalServiceType()), entity.getExternalServiceEmail());
		return new ExternalLoad(entity.getTransactionId(), toWallet, new Money(entity.getAmount().doubleValue()),
				entity.getTimestamp(), externalAccount);
	}
}