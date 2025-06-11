package edu.aseca.bags.persistence.repository;

import edu.aseca.bags.application.interfaces.ExternalAccountRepository;
import edu.aseca.bags.domain.participant.ExternalAccount;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaExternalAccountRepository implements ExternalAccountRepository {

	@Override
	public Optional<ExternalAccount> findByServiceNameAndServiceTypeAndEmail(String serviceName, String serviceType,
			String email) {
		return Optional.empty();
	}

	@Override
	public void save(ExternalAccount externalAccount) {

	}
}
