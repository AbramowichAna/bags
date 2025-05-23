package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.Transfer;

public interface TransferRepository {
	void save(Transfer transfer);

	Transfer findById(String id);

	void delete(Transfer transfer);

	boolean existsById(String id);
}
