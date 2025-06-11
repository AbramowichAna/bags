package edu.aseca.bags.application.util;

import edu.aseca.bags.application.interfaces.ExternalAccountRepository;
import edu.aseca.bags.domain.participant.ExternalAccount;
import java.util.Optional;
import java.util.Set;

public class InMemoryExternalAccountRepository implements ExternalAccountRepository {

	Set<ExternalAccount> data;

	public InMemoryExternalAccountRepository() {
		data = Set.of();
	}

	@Override
	public Optional<ExternalAccount> findByServiceNameAndServiceTypeAndEmail(String serviceName, String serviceType,
			String email) {
		return Optional.empty();
	}

	@Override
	public void save(ExternalAccount externalAccount) {

	}
}
