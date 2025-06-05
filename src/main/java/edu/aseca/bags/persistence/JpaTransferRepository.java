package edu.aseca.bags.persistence;

import edu.aseca.bags.application.TransferRepository;
import edu.aseca.bags.application.dto.PageResponse;
import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.service.PageResponseMapper;
import java.util.Optional;
import org.springframework.data.domain.Page;
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
	public PageResponse<Transfer> findByFromWalletOrToWallet(Wallet fromWallet, Wallet toWallet, Pagination page) {
		WalletEntity fromWalletEntity = walletJpaRepository.findByEmail(fromWallet.getEmail().address())
				.orElseThrow(() -> new IllegalStateException("From wallet not found"));
		WalletEntity toWalletEntity = walletJpaRepository.findByEmail(toWallet.getEmail().address())
				.orElseThrow(() -> new IllegalStateException("To wallet not found"));

		Page<TransferEntity> pageResult = jpaRepository.findByFromWalletOrToWallet(fromWalletEntity, toWalletEntity,
				org.springframework.data.domain.PageRequest.of(page.page(), page.size()));
		Page<Transfer> mappedPage = pageResult.map(TransferMapper::toDomain);

		return PageResponseMapper.fromSpringPage(mappedPage);
	}

}
