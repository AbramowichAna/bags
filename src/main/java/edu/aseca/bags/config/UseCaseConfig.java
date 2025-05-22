package edu.aseca.bags.config;

import edu.aseca.bags.application.CreateWalletUseCase;
import edu.aseca.bags.application.PasswordEncoder;
import edu.aseca.bags.application.WalletRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

	@Bean
	public CreateWalletUseCase createWalletUseCase(WalletRepository walletRepository, PasswordEncoder passwordEncoder) {
		return new CreateWalletUseCase(walletRepository, passwordEncoder);
	}
}
