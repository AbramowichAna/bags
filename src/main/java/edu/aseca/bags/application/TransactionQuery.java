package edu.aseca.bags.application;

import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.dto.TransactionView;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.domain.transaction.Transaction;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionQuery {

	private final TransferQuery transferQuery;
	private final ExternalLoadQuery externalLoadQuery;

	public TransactionQuery(TransferQuery transferQuery, ExternalLoadQuery externalLoadQuery) {
		this.transferQuery = transferQuery;
		this.externalLoadQuery = externalLoadQuery;
	}

	public List<TransactionView> getTransactions(Email walletEmail, Pagination pagination)
			throws WalletNotFoundException {
		List<Transfer> transfers = transferQuery.getTransfersForTransactionQuery(walletEmail);
		List<ExternalLoad> externalLoads = externalLoadQuery.getExternalLoadsForTransactionQuery(walletEmail);

		List<Transaction> all = new ArrayList<>();
		all.addAll(transfers);
		all.addAll(externalLoads);

		List<Transaction> sorted = all.stream().sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
				.toList();

		int fromIndex = pagination.page() * pagination.size();
		int toIndex = Math.min(fromIndex + pagination.size(), sorted.size());
		if (fromIndex > toIndex) {
			return List.of();
		}

		return sorted.subList(fromIndex, toIndex).stream()
				.map(tx -> TransactionView.from(tx, tx instanceof Transfer ? "TRANSFER" : "EXTERNAL_LOAD"))
				.collect(Collectors.toList());
	}
}