package edu.aseca.bags.config;

import edu.aseca.bags.application.*;
import edu.aseca.bags.application.CreateWalletUseCase;
import edu.aseca.bags.application.clients.BankClient;
import edu.aseca.bags.application.clients.ExternalApiClient;
import edu.aseca.bags.application.clients.ExternalApiClientImpl;
import edu.aseca.bags.application.interfaces.*;
import edu.aseca.bags.application.queries.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class UseCaseConfig {

	@Bean
	public CreateWalletUseCase createWalletUseCase(WalletRepository walletRepository, PasswordEncoder passwordEncoder) {
		return new CreateWalletUseCase(walletRepository, passwordEncoder);
	}

	@Bean
	public TransferUseCase transferUseCase(WalletRepository walletRepository, MovementRepository movementRepository,
			MovementIdGenerator movementIdGenerator) {
		return new TransferUseCase(walletRepository, movementRepository, movementIdGenerator);
	}

	@Bean
	public WalletQuery walletQuery(WalletRepository walletRepository) {
		return new WalletQuery(walletRepository);
	}

	@Bean
	public WalletMovementsQuery transferQuery(WalletRepository walletRepository, MovementQuery movementQuery) {
		return new WalletMovementsQuery(walletRepository, movementQuery);
	}

	@Bean
	public MovementQuery movementQuery(MovementRepository movementRepository) {
		return new MovementQuery(movementRepository);
	}

	@Bean
	public ExternalLoadUseCase externalLoadUseCase(WalletRepository walletRepository,
			MovementRepository movementRepository, ExternalAccountRepository externalAccountRepository,
			MovementIdGenerator gen) {
		return new ExternalLoadUseCase(walletRepository, movementRepository, externalAccountRepository, gen);
	}

	@Bean
	public RestClient restClient() {
		return RestClient.builder().build();
	}

	@Bean
	public BankClient bankClient(RestClient restClient, @Value("${bank.api.url}") String bankApiUrl) {
		return new BankClient(restClient, bankApiUrl);
	}

	@Bean
	public ExternalApiClient externalApiClient(BankClient bankClient) {
		return new ExternalApiClientImpl(List.of(bankClient));
	}

	@Bean
	public DebInUseCase debInUseCase(WalletRepository walletRepository, ExternalApiClient externalApiClient) {
		return new DebInUseCase(walletRepository, externalApiClient);
	}

}
