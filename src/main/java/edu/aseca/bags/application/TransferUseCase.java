package edu.aseca.bags.application;

import edu.aseca.bags.application.interfaces.MovementIdGenerator;
import edu.aseca.bags.application.interfaces.MovementRepository;
import edu.aseca.bags.application.interfaces.WalletRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.Movement;
import edu.aseca.bags.domain.transaction.MovementType;
import edu.aseca.bags.exception.InsufficientFundsException;
import edu.aseca.bags.exception.InvalidTransferException;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.time.Instant;

public class TransferUseCase {
	private final WalletRepository walletRepository;
	private final MovementRepository movementRepository;
	private final MovementIdGenerator movementIdGenerator;

	public TransferUseCase(WalletRepository walletRepository, MovementRepository movementRepository,
			MovementIdGenerator movementIdGenerator) {
		this.walletRepository = walletRepository;
		this.movementRepository = movementRepository;
		this.movementIdGenerator = movementIdGenerator;
	}

	public Movement execute(Email fromEmail, Email toEmail, Money amount)
			throws WalletNotFoundException, InsufficientFundsException, InvalidTransferException {

		validateMovement(fromEmail, toEmail, amount);

		Wallet fromWallet = walletRepository.findByEmail(fromEmail).orElseThrow(WalletNotFoundException::new);
		Wallet toWallet = walletRepository.findByEmail(toEmail).orElseThrow(WalletNotFoundException::new);

		if (!fromWallet.hasSufficientBalance(amount)) {
			throw new InsufficientFundsException();
		}

		fromWallet.subtractBalance(amount);
		toWallet.addBalance(amount);

		walletRepository.save(fromWallet);
		walletRepository.save(toWallet);

		Movement movement = new Movement(movementIdGenerator.generate(), fromWallet, toWallet, Instant.now(), amount,
				MovementType.TRANSFER);

		movementRepository.save(movement);
		return movement;
	}

	private void validateMovement(Email fromEmail, Email toEmail, Money amount) throws InvalidTransferException {
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