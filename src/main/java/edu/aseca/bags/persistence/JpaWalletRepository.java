package edu.aseca.bags.persistence;

import edu.aseca.bags.application.WalletRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.wallet.Wallet;
import org.springframework.stereotype.Repository;

@Repository
public class JpaWalletRepository implements WalletRepository {

	private final SpringWalletJpaRepository jpaRepository;

	public JpaWalletRepository(SpringWalletJpaRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	@Override
	public void save(Wallet wallet) {
		WalletEntity entity = WalletMapper.toEntity(wallet);
		jpaRepository.save(entity);
	}

	@Override
	public boolean existsByEmail(Email email) {
		return jpaRepository.existsByEmail(email.address());
	}
}
