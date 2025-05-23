package edu.aseca.bags.application;

import edu.aseca.bags.application.dto.WalletInfo;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.exception.BadPermissionException;
import edu.aseca.bags.exception.WalletNotFoundException;

public class WalletQuery {

	private final WalletRepository walletRepository;

	public WalletQuery(WalletRepository walletRepository) {
		this.walletRepository = walletRepository;
	}

	public WalletInfo getWalletInfoOf(String walletOwnerEmail, String requestedByEmail)
			throws WalletNotFoundException, BadPermissionException {
		Email email = new Email(walletOwnerEmail);
		Email requester = new Email(requestedByEmail);

		Wallet wallet = walletRepository.findByEmail(email).orElseThrow(WalletNotFoundException::new);

		if (!wallet.getEmail().equals(requester)) {
			throw new BadPermissionException();
		}

		return new WalletInfo(wallet.getBalance());
	}
}
