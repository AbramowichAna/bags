package edu.aseca.bags.persistence.mapper;

import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.persistence.entity.ExternalAccountEntity;
import org.springframework.stereotype.Component;

@Component
public class ExternalAccountMapper implements ParticipantEntityMapper<ExternalAccountEntity, ExternalAccount> {
	@Override
	public ExternalAccount toDomain(ExternalAccountEntity entity) {
		return new ExternalAccount(entity.getServiceName(), entity.getServiceType(), entity.getEmail());
	}

	@Override
	public ExternalAccountEntity toEntity(ExternalAccount participant) {
		return new ExternalAccountEntity(participant.externalServiceName(), participant.getServiceType().name(),
				participant.getEmail().address());
	}

	@Override
	public Class<ExternalAccountEntity> getEntityClass() {
		return ExternalAccountEntity.class;
	}

}
