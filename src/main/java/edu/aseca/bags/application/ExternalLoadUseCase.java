package edu.aseca.bags.application;

import edu.aseca.bags.api.dto.ExternalLoadRequest;
import edu.aseca.bags.api.dto.ExternalLoadResponse;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.domain.transaction.ExternalService;
import edu.aseca.bags.domain.wallet.Wallet;
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

		String wallet = request.getWalletEmail();
		BigDecimal amount = request.getAmount();
		String service = request.getExternalService();
		String externalTransactionId = request.getExternalTransactionId();
		Optional<Wallet> optionalToWallet = walletRepository.findByEmail(new Email(wallet));
		Instant timestamp = Instant.now();

		if (optionalToWallet.isEmpty()) {
			throw new WalletNotFoundException();
		}

		Wallet toWallet = optionalToWallet.get();

		if (amount == null || service == null) {
			throw new IllegalArgumentException("Amount and transfer method must not be null");
		}
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		toWallet.addBalance(new Money(amount.doubleValue()));
		walletRepository.save(toWallet);

		ExternalLoad externalLoad = new ExternalLoad(UUID.fromString(externalTransactionId), toWallet,
				new Money(amount.doubleValue()), timestamp, ExternalService.fromString(service));
		externalLoadRepository.save(externalLoad);

		return new ExternalLoadResponse(wallet, amount, service, externalTransactionId, timestamp, "SUCCESS");
	}
}