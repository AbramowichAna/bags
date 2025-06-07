package edu.aseca.bags.persistence.repository;

import edu.aseca.bags.persistence.entity.TransferEntity;
import edu.aseca.bags.persistence.entity.WalletEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringTransferJpaRepository extends JpaRepository<TransferEntity, UUID> {
	Optional<TransferEntity> findByTransferNumber(UUID transferNumber);

	Page<TransferEntity> findByFromWalletOrToWallet(WalletEntity fromWallet, WalletEntity toWallet, Pageable pageable);

	List<TransferEntity> findByFromWalletOrToWallet(WalletEntity fromWallet, WalletEntity toWallet);
}