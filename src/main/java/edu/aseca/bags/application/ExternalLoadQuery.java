package edu.aseca.bags.application;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import java.util.List;

public class ExternalLoadQuery {

	private final ExternalLoadRepository externalLoadRepository;

	public ExternalLoadQuery(ExternalLoadRepository externalLoadRepository) {
		this.externalLoadRepository = externalLoadRepository;
	}

	public List<ExternalLoad> getExternalLoadsForTransactionQuery(Email walletEmail) {
		return externalLoadRepository.findByToWalletEmail(walletEmail);
	}
}