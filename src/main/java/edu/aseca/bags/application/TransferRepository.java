package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.domain.wallet.Wallet;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransferRepository {
	void save(Transfer transfer);

	Optional<Transfer> findByTransferNumber(TransferNumber id);

	Page<Transfer> findByFromWalletOrToWallet(Wallet fromWallet, Wallet toWallet, Pageable pageable);
}
