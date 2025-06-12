package edu.aseca.bags.application.interfaces;

import edu.aseca.bags.domain.participant.ExternalAccount;
import java.util.Optional;

public interface ExternalAccountRepository {

	Optional<ExternalAccount> findByServiceNameAndServiceTypeAndEmail(String serviceName, String serviceType,
			String email);

	void save(ExternalAccount externalAccount);
}
