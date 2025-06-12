package edu.aseca.bags.persistence.mapper;

import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.persistence.entity.ParticipantEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParticipantMapper {

	private final Map<Class<? extends ParticipantEntity>, ParticipantEntityMapper<?, ?>> entityMappers = new HashMap<>();

	@Autowired
	public ParticipantMapper(List<ParticipantEntityMapper<?, ?>> mappers) {
		for (ParticipantEntityMapper<?, ?> mapper : mappers) {
			entityMappers.put(mapper.getEntityClass(), mapper);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends ParticipantEntity, D extends Participant> D toDomain(T entity) {
		ParticipantEntityMapper<T, D> mapper = (ParticipantEntityMapper<T, D>) entityMappers.get(entity.getClass());
		if (mapper == null) {
			throw new IllegalArgumentException("No mapper registered for entity " + entity.getClass());
		}
		return mapper.toDomain(entity);
	}
}
