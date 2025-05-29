package edu.aseca.bags.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringTransferJpaRepository extends JpaRepository<TransferEntity, UUID> {
	Optional<TransferEntity> findByTransferNumber(UUID transferNumber);

}