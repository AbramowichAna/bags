package edu.aseca.bags.application;

import edu.aseca.bags.api.dto.ExternalLoadRequest;
import edu.aseca.bags.application.dto.MovementView;
import edu.aseca.bags.application.interfaces.*;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.Movement;
import edu.aseca.bags.domain.transaction.MovementType;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;

public class ExternalLoadUseCase {

	private final WalletRepository walletRepository;
	private final MovementRepository movementRepository;
	private final ExternalAccountRepository externalAccountRepository;
	private final MovementIdGenerator generator;

	public ExternalLoadUseCase(WalletRepository walletRepository, MovementRepository movementRepository,
			ExternalAccountRepository externalAccountRepository, MovementIdGenerator generator) {
		this.walletRepository = walletRepository;
		this.movementRepository = movementRepository;
		this.externalAccountRepository = externalAccountRepository;
		this.generator = generator;
	}

	public MovementView loadFromExternal(ExternalLoadRequest request) throws WalletNotFoundException {

		Email walletEmail = new Email(request.walletEmail());
		BigDecimal amount = request.amount();
		String externalServiceName = request.externalServiceName();
		String externalServiceType = request.externalServiceType();
		String externalServiceEmail = request.externalServiceEmail();
		TransferNumber transactionUuid = generator.generate();
		Instant timestamp = Instant.now();
		Wallet wallet = walletRepository.findByEmail(walletEmail).orElseThrow(WalletNotFoundException::new);

		validateExternalLoad(amount, transactionUuid);

		wallet.addBalance(new Money(amount.doubleValue()));
		walletRepository.save(wallet);

		ExternalAccount externalAccount = getExternalAccount(externalServiceName, externalServiceType,
				externalServiceEmail);
		externalAccountRepository.save(externalAccount);

		Movement movement = new Movement(transactionUuid, externalAccount, wallet, timestamp,
				new Money(amount.doubleValue()), MovementType.EXTERNAL_IN);
		movementRepository.save(movement);

		return MovementView.from(movement, walletEmail);
	}

	private void validateExternalLoad(BigDecimal amount, TransferNumber id) {
		if (amount == null) {
			throw new IllegalArgumentException("Amount must not be null");
		}
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}

		if (movementRepository.findById(id).isPresent()) {
			throw new IllegalArgumentException("External reference already used");
		}
	}

	private ExternalAccount getExternalAccount(String externalServiceName, String externalServiceType,
			String externalServiceEmail) {
		return externalAccountRepository
				.findByServiceNameAndServiceTypeAndEmail(externalServiceName, externalServiceType, externalServiceEmail)
				.orElse(new ExternalAccount(externalServiceName, ServiceType.fromString(externalServiceType),
						new Email(externalServiceEmail)));
	}
}