package edu.aseca.bags.application.interfaces;

import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface TransferRepository {
	void save(Transfer transfer);

	Optional<Transfer> findByTransferNumber(TransferNumber id);

	Page<Transfer> findByFromWalletOrToWallet(Wallet fromWallet, Wallet toWallet, Pagination page);
}