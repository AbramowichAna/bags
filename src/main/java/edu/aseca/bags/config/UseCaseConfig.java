package edu.aseca.bags.config;

import edu.aseca.bags.application.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

	@Bean
	public CreateWalletUseCase createWalletUseCase(WalletRepository walletRepository, PasswordEncoder passwordEncoder) {
		return new CreateWalletUseCase(walletRepository, passwordEncoder);
	}

	@Bean
	public TransferUseCase transferUseCase(WalletRepository walletRepository, TransferRepository transferRepository) {
		return new TransferUseCase(walletRepository, transferRepository);
	}

}
