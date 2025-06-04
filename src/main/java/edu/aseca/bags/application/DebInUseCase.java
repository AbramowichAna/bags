package edu.aseca.bags.application;

import edu.aseca.bags.application.clients.ExternalApiClient;
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

	public void linkExternalService(Email walletEmail, String externalServiceName, ServiceType type, String email,
			String password) throws UnsupportedExternalService, WalletNotFoundException {

		Wallet wallet = walletRepository.findByEmail(walletEmail).orElseThrow(WalletNotFoundException::new);

		ExternalAccount externalAccount = new ExternalAccount(externalServiceName, type, email);
		boolean verified = externalApiClient.verifyCredentials(externalAccount, password);
		if (!verified) {
			throw new IllegalArgumentException("External account credentials are invalid");
		}
		wallet.linkExternalAccount(externalAccount);
		walletRepository.save(wallet);
	}

	public void requestLoadFromExternalService(Email walletEmail, String externalServiceName, ServiceType type,
			String email, double amount) throws UnsupportedExternalService, WalletNotFoundException {

		if (!externalApiClient.isSupportedService(externalServiceName, type)) {
			throw new UnsupportedExternalService(externalServiceName);
		}

		Wallet wallet = walletRepository.findByEmail(walletEmail).orElseThrow(WalletNotFoundException::new);
		ExternalAccount externalAccount = new ExternalAccount(externalServiceName, type, email);

		if (!wallet.isLinkedTo(externalAccount)) {
			throw new IllegalArgumentException("Wallet is not linked to external account");
		}

		if (!externalApiClient.requestLoad(externalAccount, new Money(amount))) {
			throw new IllegalArgumentException("Failed to request load from external service");
		}
	}

}