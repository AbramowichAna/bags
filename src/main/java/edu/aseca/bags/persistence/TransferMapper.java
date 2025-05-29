package edu.aseca.bags.persistence;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.domain.wallet.Wallet;
import java.math.BigDecimal;

public class TransferMapper {

	public static TransferEntity toEntity(Transfer transfer, WalletEntity fromWalletEntity,
			WalletEntity toWalletEntity) {
		if (transfer == null) {
			return null;
		}

		TransferEntity entity = new TransferEntity(fromWalletEntity, toWalletEntity,
				BigDecimal.valueOf(transfer.amount().amount()), transfer.timestamp());
		entity.setTransferNumber(transfer.transferNumber().value());

		return entity;
	}

	public static Transfer toDomain(TransferEntity entity) {
		if (entity == null) {
			return null;
		}

		Wallet fromWallet = WalletMapper.toDomain(entity.getFromWallet());
		Wallet toWallet = WalletMapper.toDomain(entity.getToWallet());

		return new Transfer(TransferNumber.of(entity.getTransferNumber()), fromWallet, toWallet,
				new Money(entity.getAmount().doubleValue()), entity.getTimestamp());
	}
}