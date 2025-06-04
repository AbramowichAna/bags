package edu.aseca.bags.persistence;

import edu.aseca.bags.application.ExternalLoadRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaExternalLoadRepository implements ExternalLoadRepository {

	private final SpringExternalLoadJpaRepository jpaRepository;
	private final SpringWalletJpaRepository walletJpaRepository;

	public JpaExternalLoadRepository(SpringExternalLoadJpaRepository jpaRepository,
			SpringWalletJpaRepository walletJpaRepository) {
		this.jpaRepository = jpaRepository;
		this.walletJpaRepository = walletJpaRepository;
	}

	@Override
	public void save(ExternalLoad externalLoad) throws WalletNotFoundException {
		WalletEntity toWalletEntity = walletJpaRepository.findByEmail(externalLoad.toWallet().getEmail().address())
				.orElseThrow(WalletNotFoundException::new);
		ExternalLoadEntity entity = ExternalLoadMapper.toEntity(externalLoad, toWalletEntity);
		jpaRepository.save(entity);
	}

	@Override
	public boolean existsByTransactionId(UUID transactionUuid) {
		return jpaRepository.existsByTransactionId(transactionUuid);
	}

	@Override
	public List<ExternalLoad> findByToWalletEmail(Email walletEmail) {
		List<ExternalLoadEntity> entities = jpaRepository.findByToWalletEmail(walletEmail.address());
		return entities.stream().map(entity -> {
			WalletEntity toWalletEntity = walletJpaRepository.findByEmail(entity.getToWallet().getEmail()).orElseThrow(
					() -> new RuntimeException("Wallet not found for email: " + entity.getToWallet().getEmail()));
			return ExternalLoadMapper.toDomain(entity, WalletMapper.toDomain(toWalletEntity));
		}).toList();
	}
}