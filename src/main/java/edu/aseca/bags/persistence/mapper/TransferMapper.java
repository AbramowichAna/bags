package edu.aseca.bags.persistence.mapper;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.persistence.entity.TransferEntity;
import edu.aseca.bags.persistence.entity.WalletEntity;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class TransferMapper {

	private final WalletMapper walletMapper;

	public TransferMapper(WalletMapper walletMapper) {
		this.walletMapper = walletMapper;
	}

	public TransferEntity toEntity(Transfer transfer, WalletEntity fromWalletEntity, WalletEntity toWalletEntity) {
		if (transfer == null) {
			return null;
		}

		TransferEntity entity = new TransferEntity(transfer.transferNumber().value(), fromWalletEntity, toWalletEntity,
				BigDecimal.valueOf(transfer.amount().amount()), transfer.timestamp());
		entity.setTransferNumber(transfer.transferNumber().value());

		return entity;
	}

	public Transfer toDomain(TransferEntity entity) {
		if (entity == null) {
			return null;
		}

		Wallet fromWallet = walletMapper.toDomain(entity.getFromWallet());
		Wallet toWallet = walletMapper.toDomain(entity.getToWallet());

		return new Transfer(TransferNumber.of(entity.getTransferNumber()), fromWallet, toWallet,
				new Money(entity.getAmount().doubleValue()), entity.getTimestamp());
	}
}