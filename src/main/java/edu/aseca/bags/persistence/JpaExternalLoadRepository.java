package edu.aseca.bags.persistence;

import edu.aseca.bags.application.ExternalLoadRepository;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.exception.WalletNotFoundException;
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
}