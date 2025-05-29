package edu.aseca.bags.application;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.exception.InsufficientFundsException;
import edu.aseca.bags.exception.InvalidTransferException;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class TransferUseCase {
	private final WalletRepository walletRepository;
	private final TransferRepository transferRepository;
	private final TransferNumberGenerator transferNumberGenerator;

	public TransferUseCase(WalletRepository walletRepository, TransferRepository transferRepository,
			TransferNumberGenerator transferNumberGenerator) {
		this.walletRepository = walletRepository;
		this.transferRepository = transferRepository;
		this.transferNumberGenerator = transferNumberGenerator;
	}

	public Transfer execute(Email fromEmail, Email toEmail, Money amount)
			throws WalletNotFoundException, InsufficientFundsException, InvalidTransferException {

		boolean triesToTransferItself = fromEmail.equals(toEmail);

		if (triesToTransferItself) {
			throw new InvalidTransferException("Cannot transfer to the same wallet");
		}

		Wallet fromWallet = walletRepository.findByEmail(fromEmail).orElseThrow(WalletNotFoundException::new);
		Wallet toWallet = walletRepository.findByEmail(toEmail).orElseThrow(WalletNotFoundException::new);

		try {
			fromWallet.subtractBalance(amount);
			toWallet.addBalance(amount);

			walletRepository.save(fromWallet);
			walletRepository.save(toWallet);

			Transfer transfer = new Transfer(transferNumberGenerator.generate(), fromWallet, toWallet, amount,
					Instant.now());
			transferRepository.save(transfer);

			return transfer;
		} catch (IllegalStateException e) {
			throw new InsufficientFundsException();
		}
	}

	public Page<Transfer> getTransfers(Email email, Pageable pageable) throws WalletNotFoundException {

		Optional<Wallet> optionalWallet = walletRepository.findByEmail(email);
		if (optionalWallet.isEmpty()) {
			throw new WalletNotFoundException();
		}

		Wallet wallet = optionalWallet.get();
		return transferRepository.findByFromWalletOrToWallet(wallet, wallet, pageable);
	}
}