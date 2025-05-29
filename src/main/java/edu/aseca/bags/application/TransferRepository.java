package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.Transfer;
import java.util.Optional;
import java.util.UUID;

public interface TransferRepository {
	void save(Transfer transfer);

	Optional<Transfer> findById(UUID id);

	void delete(Transfer transfer);

	boolean existsById(UUID id);
}
