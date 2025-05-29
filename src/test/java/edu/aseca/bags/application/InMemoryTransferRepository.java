package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class InMemoryTransferRepository implements TransferRepository {
	private final Map<UUID, Transfer> data = new HashMap<>();

	@Override
	public void save(Transfer transfer) {
		data.put(transfer.transferNumber().value(), transfer);
	}

	@Override
	public Optional<Transfer> findByTransferNumber(TransferNumber id) {
		return Optional.ofNullable(data.get(id.value()));
	}

	public int count() {
		return data.size();
	}

	public void clear() {
		data.clear();
	}
}