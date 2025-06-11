package edu.aseca.bags.persistence.repository;

import edu.aseca.bags.application.interfaces.WalletRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.persistence.entity.WalletEntity;
import edu.aseca.bags.persistence.mapper.WalletMapper;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaWalletRepository implements WalletRepository {

	private final SpringWalletJpaRepository jpaRepository;
	private final WalletMapper walletMapper;

	public JpaWalletRepository(SpringWalletJpaRepository jpaRepository, WalletMapper walletMapper) {
		this.jpaRepository = jpaRepository;
		this.walletMapper = walletMapper;
	}

	@Override
	public void save(Wallet wallet) {
		Optional<WalletEntity> existingEntityOpt = jpaRepository.findByEmail(wallet.getEmail().address());

		WalletEntity entity = existingEntityOpt.map(existing -> {
			existing.setBalance(BigDecimal.valueOf(wallet.getBalance().amount()));
			return existing;
		}).orElseGet(() -> walletMapper.toEntity(wallet));

		jpaRepository.save(entity);
	}

	@Override
	public boolean existsByEmail(Email email) {
		return jpaRepository.existsByEmail(email.address());
	}

	@Override
	public Optional<Wallet> findByEmail(Email email) {
		return jpaRepository.findByEmail(email.address()).map(walletMapper::toDomain);
	}
}
