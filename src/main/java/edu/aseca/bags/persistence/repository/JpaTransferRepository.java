package edu.aseca.bags.persistence.repository;

import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.interfaces.TransferRepository;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.persistence.entity.TransferEntity;
import edu.aseca.bags.persistence.entity.WalletEntity;
import edu.aseca.bags.persistence.mapper.TransferMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
public class JpaTransferRepository implements TransferRepository {

	private final SpringTransferJpaRepository jpaRepository;
	private final SpringWalletJpaRepository walletJpaRepository;
	private final TransferMapper transferMapper;

	public JpaTransferRepository(SpringTransferJpaRepository jpaRepository,
			SpringWalletJpaRepository walletJpaRepository, TransferMapper transferMapper) {
		this.jpaRepository = jpaRepository;
		this.walletJpaRepository = walletJpaRepository;
		this.transferMapper = transferMapper;
	}

	@Override
	public void save(Transfer transfer) {
		WalletEntity fromWalletEntity = walletJpaRepository.findByEmail(transfer.fromWallet().getEmail().address())
				.orElseThrow(() -> new IllegalStateException("From wallet not found"));
		WalletEntity toWalletEntity = walletJpaRepository.findByEmail(transfer.toWallet().getEmail().address())
				.orElseThrow(() -> new IllegalStateException("To wallet not found"));

		TransferEntity entity = transferMapper.toEntity(transfer, fromWalletEntity, toWalletEntity);
		jpaRepository.save(entity);
	}

	@Override
	public Optional<Transfer> findByTransferNumber(TransferNumber id) {
		return jpaRepository.findByTransferNumber(id.value()).map(transferMapper::toDomain);
	}

	@Override
	public List<Transfer> findByFromWalletOrToWallet(Wallet fromWallet, Wallet toWallet, Pagination page) {
		WalletEntity fromWalletEntity = walletJpaRepository.findByEmail(fromWallet.getEmail().address())
				.orElseThrow(() -> new IllegalStateException("From wallet not found"));
		WalletEntity toWalletEntity = walletJpaRepository.findByEmail(toWallet.getEmail().address())
				.orElseThrow(() -> new IllegalStateException("To wallet not found"));

		if (page == null) {
			List<TransferEntity> entities = jpaRepository.findByFromWalletOrToWallet(fromWalletEntity, toWalletEntity);
			return entities.stream().map(transferMapper::toDomain).toList();
		}

		Page<TransferEntity> pageResult = jpaRepository.findByFromWalletOrToWallet(fromWalletEntity, toWalletEntity,
				org.springframework.data.domain.PageRequest.of(page.page(), page.size()));
		return pageResult.stream().map(transferMapper::toDomain).toList();
	}

}
