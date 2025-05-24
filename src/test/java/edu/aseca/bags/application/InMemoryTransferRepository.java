package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.Transfer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class InMemoryTransferRepository implements TransferRepository {
	private final Map<String, Transfer> data = new HashMap<>();

	@Override
	public void save(Transfer transfer) {
		String id = UUID.randomUUID().toString();
		data.put(id, transfer);
	}

	@Override
	public Transfer findById(String id) {
		return data.get(id);
	}

	@Override
	public void delete(Transfer transfer) {
		data.entrySet().removeIf(entry -> entry.getValue().equals(transfer));
	}

	@Override
	public boolean existsById(String id) {
		return data.containsKey(id);
	}

	public int count() {
		return data.size();
	}

	public void clear() {
		data.clear();
	}
}