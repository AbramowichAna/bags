package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import java.util.Optional;

public interface TransferRepository {
	void save(Transfer transfer);

	Optional<Transfer> findByTransferNumber(TransferNumber id);

}
