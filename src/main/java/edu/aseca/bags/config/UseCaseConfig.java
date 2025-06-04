package edu.aseca.bags.config;

import edu.aseca.bags.application.*;
import edu.aseca.bags.application.CreateWalletUseCase;
import edu.aseca.bags.application.PasswordEncoder;
import edu.aseca.bags.application.WalletQuery;
import edu.aseca.bags.application.WalletRepository;
import edu.aseca.bags.application.clients.ExternalApiClient;
import edu.aseca.bags.application.clients.ExternalApiClientImpl;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

	@Bean
	public CreateWalletUseCase createWalletUseCase(WalletRepository walletRepository, PasswordEncoder passwordEncoder) {
		return new CreateWalletUseCase(walletRepository, passwordEncoder);
	}

	@Bean
	public TransferUseCase transferUseCase(WalletRepository walletRepository, TransferRepository transferRepository,
			TransferNumberGenerator transferNumberGenerator) {
		return new TransferUseCase(walletRepository, transferRepository, transferNumberGenerator);
	}

	@Bean
	public WalletQuery walletQuery(WalletRepository walletRepository) {
		return new WalletQuery(walletRepository);
	}

	@Bean
	public TransferQuery transferQuery(WalletRepository walletRepository, TransferRepository transferRepository) {
		return new TransferQuery(walletRepository, transferRepository);
	}

	@Bean
	public ExternalLoadUseCase externalLoadUseCase(WalletRepository walletRepository,
			ExternalLoadRepository externalLoadRepository) {
		return new ExternalLoadUseCase(walletRepository, externalLoadRepository);
	}

	@Bean
	public ExternalLoadQuery externalLoadQuery(ExternalLoadRepository externalLoadRepository) {
		return new ExternalLoadQuery(externalLoadRepository);
	}

	@Bean
	public TransactionQuery transactionQuery(TransferQuery transferQuery, ExternalLoadQuery externalLoadQuery) {
		return new TransactionQuery(transferQuery, externalLoadQuery);
	}
}
