package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.ExternalLoad;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryExternalLoadRepository implements ExternalLoadRepository {
	private final Map<UUID, ExternalLoad> data = new HashMap<>();

	@Override
	public void save(ExternalLoad externalLoad) {
		data.put(externalLoad.transactionId(), externalLoad);
	}

	public int count() {
		return data.size();
	}

	public void clear() {
		data.clear();
	}
}