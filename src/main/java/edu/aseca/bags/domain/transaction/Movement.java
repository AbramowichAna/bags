package edu.aseca.bags.domain.transaction;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Participant;
import java.time.Instant;
import java.util.Objects;

public record Movement(MovementId movementId, Participant from, Participant to, Instant timestamp, Money amount,
		MovementType type) {
	public Movement {
		Objects.requireNonNull(movementId, "Movement ID must be defined");
		Objects.requireNonNull(from, "From participant must be defined");
		Objects.requireNonNull(to, "To participant must be defined");
		Objects.requireNonNull(timestamp, "Timestamp must be defined");
		Objects.requireNonNull(amount, "Amount must be defined");
		Objects.requireNonNull(type, "Type must be defined");
	}
}
