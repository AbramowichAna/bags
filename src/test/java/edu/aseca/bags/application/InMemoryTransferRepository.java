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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
	public Page<Transfer> findByFromWalletOrToWallet(Wallet fromWallet, Wallet toWallet, Pageable pageable) {
		List<Transfer> filteredTransfers = data.values().stream()
				.filter(transfer -> transfer.fromWallet().equals(fromWallet) || transfer.toWallet().equals(toWallet))
				.collect(Collectors.toList());

		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), filteredTransfers.size());

		List<Transfer> paginatedTransfers = filteredTransfers.subList(start, end);

		return new PageImpl<>(paginatedTransfers, pageable, filteredTransfers.size());
	}

	public int count() {
		return data.size();
	}

	public void clear() {
		data.clear();
	}
}