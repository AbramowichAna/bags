package edu.aseca.bags.application;

import edu.aseca.bags.api.dto.ExternalLoadRequest;
import edu.aseca.bags.api.dto.ExternalLoadResponse;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class ExternalLoadUseCase {

	private final WalletRepository walletRepository;
	private final ExternalLoadRepository externalLoadRepository;

	public ExternalLoadUseCase(WalletRepository walletRepository, ExternalLoadRepository externalLoadRepository) {
		this.walletRepository = walletRepository;
		this.externalLoadRepository = externalLoadRepository;
	}

	public ExternalLoadResponse loadFromExternal(ExternalLoadRequest request) throws WalletNotFoundException {

		String wallet = request.walletEmail();
		BigDecimal amount = request.amount();
		String externalServiceName = request.externalServiceName();
		String externalServiceType = request.externalServiceType();
		String externalServiceEmail = request.externalServiceEmail();
		String externalTransactionId = request.externalTransactionId();
		Optional<Wallet> optionalToWallet = walletRepository.findByEmail(new Email(wallet));
		Instant timestamp = Instant.now();

		Wallet toWallet = optionalToWallet.orElseThrow(WalletNotFoundException::new);

		if (amount == null) {
			throw new IllegalArgumentException("Amount must not be null");
		}
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}

		UUID transactionUuid = UUID.fromString(externalTransactionId);
		if (externalLoadRepository.existsByTransactionId(transactionUuid)) {
			throw new IllegalArgumentException("External reference already used");
		}

		toWallet.addBalance(new Money(amount.doubleValue()));
		walletRepository.save(toWallet);

		ExternalAccount externalAccount = new ExternalAccount(externalServiceName,
				ServiceType.fromString(externalServiceType), externalServiceEmail);

		ExternalLoad externalLoad = new ExternalLoad(UUID.fromString(externalTransactionId), toWallet,
				new Money(amount.doubleValue()), timestamp, externalAccount);
		externalLoadRepository.save(externalLoad);

		return new ExternalLoadResponse(externalLoad.toWallet().getEmail().address(), externalLoad.amount().amount(),
				externalLoad.externalAccount().externalServiceName(),
				externalLoad.externalAccount().serviceType().name(), externalLoad.externalAccount().email(),
				externalLoad.transactionId().toString(), timestamp, "SUCCESS");
	}
}