package edu.aseca.bags.persistence.mapper;

import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.persistence.entity.ParticipantEntity;

public interface ParticipantEntityMapper<T extends ParticipantEntity, E extends Participant> {
	E toDomain(T entity);

	T toEntity(E participant);

	Class<T> getEntityClass();
}
