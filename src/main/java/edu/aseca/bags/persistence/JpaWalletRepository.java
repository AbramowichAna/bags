package edu.aseca.bags.persistence;

import edu.aseca.bags.application.WalletRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.wallet.Wallet;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaWalletRepository implements WalletRepository {

	private final SpringWalletJpaRepository jpaRepository;

	public JpaWalletRepository(SpringWalletJpaRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	@Override
	public void save(Wallet wallet) {
		Optional<WalletEntity> existingEntityOpt = jpaRepository.findByEmail(wallet.getEmail().address());

		WalletEntity entity = existingEntityOpt.map(existing -> {
			existing.setBalance(BigDecimal.valueOf(wallet.getBalance().amount()));
			return existing;
		}).orElseGet(() -> WalletMapper.toEntity(wallet));

		jpaRepository.save(entity);
	}

	@Override
	public boolean existsByEmail(Email email) {
		return jpaRepository.existsByEmail(email.address());
	}

	@Override
	public Optional<Wallet> findByEmail(Email email) {
		return jpaRepository.findByEmail(email.address()).map(WalletMapper::toDomain);
	}
}
