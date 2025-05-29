package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.Transfer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class InMemoryTransferRepository implements TransferRepository {
	private final Map<UUID, Transfer> data = new HashMap<>();

	@Override
	public void save(Transfer transfer) {
		data.put(transfer.transferNumber(), transfer);
	}

	@Override
	public Optional<Transfer> findById(UUID id) {
		return Optional.ofNullable(data.get(id));
	}

	@Override
	public void delete(Transfer transfer) {
		data.entrySet().removeIf(entry -> entry.getValue().equals(transfer));
	}

	@Override
	public boolean existsById(UUID id) {
		return data.containsKey(id);
	}

	public int count() {
		return data.size();
	}

	public void clear() {
		data.clear();
	}
}