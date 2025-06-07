package edu.aseca.bags.persistence.repository;

import edu.aseca.bags.persistence.entity.ExternalLoadEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringExternalLoadJpaRepository extends JpaRepository<ExternalLoadEntity, UUID> {
	boolean existsByTransactionId(UUID transactionUuid);

	List<ExternalLoadEntity> findByToWalletEmail(String address);
}
