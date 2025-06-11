package edu.aseca.bags.persistence.repository;

import edu.aseca.bags.persistence.entity.ExternalAccountEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringExternalAccountJpaRepository extends JpaRepository<ExternalAccountEntity, UUID> {
	Optional<ExternalAccountEntity> findByServiceNameAndServiceTypeAndEmail(String serviceName, String serviceType,
			String email);
}
