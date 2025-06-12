package edu.aseca.bags.persistence.mapper.participantstrategy;

import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.persistence.entity.ExternalAccountEntity;
import edu.aseca.bags.persistence.entity.ParticipantEntity;
import edu.aseca.bags.persistence.mapper.ExternalAccountMapper;
import edu.aseca.bags.persistence.repository.SpringExternalAccountJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ExternalAccountResolver implements ParticipantEntityResolverStrategy {

	private final SpringExternalAccountJpaRepository repository;
	private final ExternalAccountMapper externalAccountMapper;

	public ExternalAccountResolver(SpringExternalAccountJpaRepository repository,
			ExternalAccountMapper externalAccountMapper) {
		this.repository = repository;
		this.externalAccountMapper = externalAccountMapper;
	}

	@Override
	public boolean supports(Participant participant) {
		return participant instanceof ExternalAccount;
	}

	@Override
	public ParticipantEntity resolve(Participant participant) {
		ExternalAccount acc = (ExternalAccount) participant;
		Optional<ExternalAccountEntity> byServiceNameAndServiceTypeAndEmail = repository
				.findByServiceNameAndServiceTypeAndEmail(acc.externalServiceName(), acc.getServiceType().name(),
						acc.getEmail().address());
		return byServiceNameAndServiceTypeAndEmail
				.orElseGet(() -> repository.save(externalAccountMapper.toEntity(acc)));
	}
}
