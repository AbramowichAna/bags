package edu.aseca.bags.application.util;

import edu.aseca.bags.application.TransferRepository;
import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import java.util.*;

public class InMemoryTransferRepository implements TransferRepository {
	private final Map<UUID, Transfer> data = new HashMap<>();

	@Override
	public void save(Transfer transfer) {
		data.put(transfer.transferNumber().value(), transfer);
	}

	@Override
	public Optional<Transfer> findByTransferNumber(TransferNumber id) {
		return Optional.ofNullable(data.get(id.value()));
	}

	@Override
	public List<Transfer> findByFromWalletOrToWallet(Wallet from, Wallet to, Pagination page) {
		List<Transfer> result = data.values().stream()
				.filter(t -> t.fromWallet().equals(from) || t.toWallet().equals(to))
				.sorted(Comparator.comparing(Transfer::timestamp).reversed()).toList();

		if (page == null) {
			return result;
		}

		int fromIndex = page.page() * page.size();
		int toIndex = Math.min(fromIndex + page.size(), result.size());
		if (fromIndex > toIndex) {
			return List.of();
		}
		return result.subList(fromIndex, toIndex);
	}

	public int count() {
		return data.size();
	}

	public void clear() {
		data.clear();
	}
}