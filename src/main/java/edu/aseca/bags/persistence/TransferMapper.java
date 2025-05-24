package edu.aseca.bags.persistence;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.wallet.Wallet;

public class TransferMapper {

	public static TransferEntity toEntity(Transfer transfer) {
		if (transfer == null) {
			return null;
		}

		WalletEntity fromWalletEntity = WalletMapper.toEntity(transfer.fromWallet());
		WalletEntity toWalletEntity = WalletMapper.toEntity(transfer.toWallet());

		return new TransferEntity(fromWalletEntity, toWalletEntity, transfer.amount().amount(), transfer.timestamp());
	}

	public static Transfer toDomain(TransferEntity entity) {
		if (entity == null) {
			return null;
		}

		Wallet fromWallet = WalletMapper.toDomain(entity.getFromWallet());
		Wallet toWallet = WalletMapper.toDomain(entity.getToWallet());

		return new Transfer(fromWallet, toWallet, new Money(entity.getAmount()), entity.getTimestamp());
	}
}