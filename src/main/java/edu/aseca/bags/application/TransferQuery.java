package edu.aseca.bags.application;

import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.dto.TransferView;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.util.List;
import java.util.Optional;

public class TransferQuery {
	private final WalletRepository walletRepository;
	private final TransferRepository transferRepository;

	public TransferQuery(WalletRepository walletRepository, TransferRepository transferRepository) {
		this.walletRepository = walletRepository;
		this.transferRepository = transferRepository;
	}

	public List<TransferView> getTransfers(Email email, Pagination page) throws WalletNotFoundException {
		Optional<Wallet> optionalWallet = walletRepository.findByEmail(email);
		if (optionalWallet.isEmpty()) {
			throw new WalletNotFoundException();
		}
		Wallet wallet = optionalWallet.get();
		return transferRepository.findByFromWalletOrToWallet(wallet, wallet, page).stream()
				.map(t -> new TransferView(t, email)).toList();
	}

	public List<Transfer> getTransfersForTransactionQuery(Email email) throws WalletNotFoundException {
		Optional<Wallet> optionalWallet = walletRepository.findByEmail(email);
		if (optionalWallet.isEmpty()) {
			throw new WalletNotFoundException();
		}
		Wallet wallet = optionalWallet.get();
		return transferRepository.findByFromWalletOrToWallet(wallet, wallet, null);
	}
}