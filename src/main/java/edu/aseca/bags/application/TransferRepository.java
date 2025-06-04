package edu.aseca.bags.application;

import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import java.util.List;
import java.util.Optional;

public interface TransferRepository {
	void save(Transfer transfer);

	Optional<Transfer> findByTransferNumber(TransferNumber id);

	List<Transfer> findByFromWalletOrToWallet(Wallet fromWallet, Wallet toWallet, Pagination page);
}