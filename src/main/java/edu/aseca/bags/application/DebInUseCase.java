package edu.aseca.bags.application;

import edu.aseca.bags.application.clients.ExternalApiClient;
import edu.aseca.bags.application.interfaces.WalletRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.exception.UnsupportedExternalService;
import edu.aseca.bags.exception.WalletNotFoundException;

public class DebInUseCase {

	private final WalletRepository walletRepository;
	private final ExternalApiClient externalApiClient;

	public DebInUseCase(WalletRepository walletRepository, ExternalApiClient externalApiClient) {
		this.walletRepository = walletRepository;
		this.externalApiClient = externalApiClient;
	}

	public void requestDebIn(Email walletEmail, String externalServiceName, ServiceType type, String email,
			double amount) throws UnsupportedExternalService, WalletNotFoundException {

		if (walletEmail == null || externalServiceName == null || type == null) {
			throw new IllegalArgumentException("Parameters cannot be null");
		}

		Email externalEmail = new Email(email);

		if (!externalApiClient.isSupportedService(externalServiceName, type)) {
			throw new UnsupportedExternalService(externalServiceName);
		}

		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}

		walletRepository.findByEmail(walletEmail).orElseThrow(WalletNotFoundException::new);
		ExternalAccount externalAccount = new ExternalAccount(externalServiceName, type, externalEmail);

		if (!externalApiClient.requestLoad(externalAccount, new Money(amount), walletEmail)) {
			throw new IllegalArgumentException("Failed to request load from external service");
		}
	}

}