package edu.aseca.bags.persistence;

import edu.aseca.bags.application.TransferRepository;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import java.util.Optional;
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
}
