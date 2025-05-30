package edu.aseca.bags.persistence;

import edu.aseca.bags.application.TransferRepository;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.domain.wallet.Wallet;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class JpaTransferRepository implements TransferRepository {

	private final SpringTransferJpaRepository jpaRepository;
	private final SpringWalletJpaRepository walletJpaRepository;

	public JpaTransferRepository(SpringTransferJpaRepository jpaRepository,
			SpringWalletJpaRepository walletJpaRepository) {
		this.jpaRepository = jpaRepository;
		this.walletJpaRepository = walletJpaRepository;
	}

	@Override
	public void save(Transfer transfer) {
		WalletEntity fromWalletEntity = walletJpaRepository.findByEmail(transfer.fromWallet().getEmail().address())
				.orElseThrow(() -> new IllegalStateException("From wallet not found"));
		WalletEntity toWalletEntity = walletJpaRepository.findByEmail(transfer.toWallet().getEmail().address())
				.orElseThrow(() -> new IllegalStateException("To wallet not found"));

		TransferEntity entity = TransferMapper.toEntity(transfer, fromWalletEntity, toWalletEntity);
		jpaRepository.save(entity);
	}

	@Override
	public Optional<Transfer> findByTransferNumber(TransferNumber id) {
		return jpaRepository.findByTransferNumber(id.value()).map(TransferMapper::toDomain);
	}

	@Override
	public List<Transfer> findByFromWalletOrToWallet(Wallet fromWallet, Wallet toWallet, int page, int size) {
		WalletEntity fromWalletEntity = walletJpaRepository.findByEmail(fromWallet.getEmail().address())
				.orElseThrow(() -> new IllegalStateException("From wallet not found"));
		WalletEntity toWalletEntity = walletJpaRepository.findByEmail(toWallet.getEmail().address())
				.orElseThrow(() -> new IllegalStateException("To wallet not found"));

		Page<TransferEntity> pageResult = jpaRepository.findByFromWalletOrToWallet(
				fromWalletEntity, toWalletEntity, org.springframework.data.domain.PageRequest.of(page, size));
		return pageResult.stream().map(TransferMapper::toDomain).toList();
	}

}
