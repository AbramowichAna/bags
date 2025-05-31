package edu.aseca.bags.persistence;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.domain.transaction.ExternalService;
import java.math.BigDecimal;

public class ExternalLoadMapper {

	public static ExternalLoadEntity toEntity(ExternalLoad externalLoad, WalletEntity toWalletEntity) {
		return new ExternalLoadEntity(externalLoad.transactionId(), toWalletEntity,
				BigDecimal.valueOf(externalLoad.amount().amount()), externalLoad.timestamp(),
				externalLoad.service().name());
	}

	public static ExternalLoad toDomain(ExternalLoadEntity entity, edu.aseca.bags.domain.wallet.Wallet toWallet) {
		return new ExternalLoad(entity.getTransactionId(), toWallet, new Money(entity.getAmount().doubleValue()),
				entity.getTimestamp(), ExternalService.valueOf(entity.getService()));
	}
}