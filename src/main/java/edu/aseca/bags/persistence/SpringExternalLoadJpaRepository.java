package edu.aseca.bags.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringExternalLoadJpaRepository extends JpaRepository<ExternalLoadEntity, UUID> {
	boolean existsByTransactionId(UUID transactionUuid);
}
