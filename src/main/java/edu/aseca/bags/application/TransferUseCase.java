package edu.aseca.bags.application;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.exception.InsufficientFundsException;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class TransferUseCase {
	private final WalletRepository walletRepository;
	private final TransferRepository transferRepository;

	public TransferUseCase(WalletRepository walletRepository, TransferRepository transferRepository) {
		this.walletRepository = walletRepository;
		this.transferRepository = transferRepository;
	}

	public Transfer execute(Email fromEmail, Email toEmail, Money amount)
			throws WalletNotFoundException, InsufficientFundsException {
		Wallet fromWallet = walletRepository.findByEmail(fromEmail).orElseThrow(WalletNotFoundException::new);

		Wallet toWallet = walletRepository.findByEmail(toEmail).orElseThrow(WalletNotFoundException::new);

		try {
			fromWallet.subtractBalance(amount);
			toWallet.addBalance(amount);

			walletRepository.save(fromWallet);
			walletRepository.save(toWallet);

			Transfer transfer = new Transfer(fromWallet, toWallet, amount, Instant.now());
			transferRepository.save(transfer);

			return transfer;
		} catch (IllegalStateException e) {
			throw new InsufficientFundsException();
		}
	}
}