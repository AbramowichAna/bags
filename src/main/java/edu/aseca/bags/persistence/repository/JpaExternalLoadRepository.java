package edu.aseca.bags.persistence.repository;

import edu.aseca.bags.application.interfaces.ExternalLoadRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.persistence.entity.ExternalLoadEntity;
import edu.aseca.bags.persistence.entity.WalletEntity;
import edu.aseca.bags.persistence.mapper.ExternalLoadMapper;
import edu.aseca.bags.persistence.mapper.WalletMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaExternalLoadRepository implements ExternalLoadRepository {

	private final SpringExternalLoadJpaRepository jpaRepository;
	private final SpringWalletJpaRepository walletJpaRepository;
	private final WalletMapper walletMapper;

	public JpaExternalLoadRepository(SpringExternalLoadJpaRepository jpaRepository,
			SpringWalletJpaRepository walletJpaRepository, WalletMapper walletMapper) {
		this.jpaRepository = jpaRepository;
		this.walletJpaRepository = walletJpaRepository;
		this.walletMapper = walletMapper;
	}

	@Override
	public void save(ExternalLoad externalLoad) throws WalletNotFoundException {
		WalletEntity toWalletEntity = walletJpaRepository.findByEmail(externalLoad.toWallet().getEmail().address())
				.orElseThrow(WalletNotFoundException::new);
		ExternalLoadEntity entity = ExternalLoadMapper.toEntity(externalLoad, toWalletEntity);
		jpaRepository.save(entity);
	}
}