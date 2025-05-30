package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.domain.wallet.Wallet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

	@Override
	public List<Transfer> findByFromWalletOrToWallet(Wallet fromWallet, Wallet toWallet, int page, int size) {
		List<Transfer> filteredTransfers = data.values().stream()
				.filter(transfer -> transfer.fromWallet().equals(fromWallet) || transfer.toWallet().equals(toWallet))
				.collect(Collectors.toList());

		int start = page * size;
		int end = Math.min(start + size, filteredTransfers.size());
		if (start > end) {
			return List.of();
		}

		return filteredTransfers.subList(start, end);
	}

	public int count() {
		return data.size();
	}

	public void clear() {
		data.clear();
	}
}