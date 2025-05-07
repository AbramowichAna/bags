package edu.aseca.bags.domain;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.InsufficientFundsException;
import edu.aseca.bags.domain.transaction.Transaction;
import edu.aseca.bags.domain.transaction.TransactionType;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.domain.wallet.WalletNotFoundException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService {

	@Autowired
	private WalletRepository walletRepository;
	@Autowired
	private TransferRepository transferRepository;

	@Transactional
	public Transaction transfer(Email fromEmail, Email toEmail, Money amount)
			throws WalletNotFoundException, InsufficientFundsException {
		Wallet from = walletRepository.findByEmail(fromEmail).orElseThrow(WalletNotFoundException::new);
		Wallet to = walletRepository.findByEmail(toEmail).orElseThrow(WalletNotFoundException::new);

		Transaction transfer = transfer(from, to, amount);
		transfer = transferRepository.save(transfer);

		from.addSentTransaction(transfer);
		to.addReceivedTransaction(transfer);
		walletRepository.save(from);
		walletRepository.save(to);

		return transfer;
	}

	private Transaction transfer(Wallet from, Wallet to, Money amount) throws InsufficientFundsException {
		try {
			from.subtractBalance(amount);
			to.addBalance(amount);
		} catch (IllegalStateException e) {
			throw new InsufficientFundsException();
		}
		return new Transaction(from, to, amount, Instant.now(), TransactionType.TRANSFER);
	}
}
