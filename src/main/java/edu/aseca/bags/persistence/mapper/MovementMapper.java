package edu.aseca.bags.persistence.mapper;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Movement;
import edu.aseca.bags.domain.transaction.MovementId;
import edu.aseca.bags.domain.transaction.MovementType;
import edu.aseca.bags.persistence.entity.MovementEntity;
import edu.aseca.bags.persistence.entity.ParticipantEntity;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MovementMapper {

	ParticipantMapper participantMapper;

	public MovementMapper(ParticipantMapper participantMapper) {
		this.participantMapper = participantMapper;
	}

	public MovementEntity toEntity(Movement movement, ParticipantEntity from, ParticipantEntity to) {
		return new MovementEntity(movement.movementId().value().toString(), from, to, movement.timestamp(),
				BigDecimal.valueOf(movement.amount().amount()), movement.type().name());
	}

	public Movement toDomain(MovementEntity entity) {
		if (entity == null) {
			return null;
		}

		return new Movement(MovementId.of(UUID.fromString(entity.getTransferNumber())),
				participantMapper.toDomain(entity.getFrom()), participantMapper.toDomain(entity.getTo()),
				entity.getTimestamp(), new Money(entity.getAmount().doubleValue()),
				MovementType.valueOf(entity.getType()));
	}
}
