package edu.aseca.bags.application;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.exception.InsufficientFundsException;
import edu.aseca.bags.exception.InvalidTransferException;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.time.Instant;

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

		validateTransfer(fromEmail, toEmail, amount);

		Wallet fromWallet = walletRepository.findByEmail(fromEmail).orElseThrow(WalletNotFoundException::new);
		Wallet toWallet = walletRepository.findByEmail(toEmail).orElseThrow(WalletNotFoundException::new);

		if (!fromWallet.hasSufficientBalance(amount)) {
			throw new InsufficientFundsException();
		}

		fromWallet.subtractBalance(amount);
		toWallet.addBalance(amount);

		walletRepository.save(fromWallet);
		walletRepository.save(toWallet);

		Transfer transfer = new Transfer(transferNumberGenerator.generate(), fromWallet, toWallet, amount,
				Instant.now());

		transferRepository.save(transfer);
		return transfer;
	}

	private void validateTransfer(Email fromEmail, Email toEmail, Money amount) throws InvalidTransferException {
		if (fromEmail == null || toEmail == null || amount == null) {
			throw new InvalidTransferException("Sender, receiver, and amount must not be null");
		}
		if (fromEmail.equals(toEmail)) {
			throw new InvalidTransferException("Cannot transfer to the same wallet");
		}
		if (amount.amount() == 0) {
			throw new InvalidTransferException("Transfer amount must be greater than zero");
		}
	}
}