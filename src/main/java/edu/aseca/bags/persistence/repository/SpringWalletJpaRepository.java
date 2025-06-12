package edu.aseca.bags.persistence.repository;

import edu.aseca.bags.persistence.entity.WalletEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringWalletJpaRepository extends JpaRepository<WalletEntity, Long> {
	Optional<WalletEntity> findByEmail(String email);

	boolean existsByEmail(String email);
}
