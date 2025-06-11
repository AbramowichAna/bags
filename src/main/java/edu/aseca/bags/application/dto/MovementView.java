package edu.aseca.bags.application.dto;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.transaction.Movement;
import java.math.BigDecimal;

public record MovementView(String id, ParticipantDto fromParticipant, ParticipantDto toParticipant, String timestamp,
		BigDecimal amount, String type) {
	public static MovementView from(Movement movement, Email participantEmail) {
		return new MovementView(movement.movementId().toString(), ParticipantDto.from(movement.from()),
				ParticipantDto.from(movement.to()), movement.timestamp().toString(),
				BigDecimal.valueOf(movement.amount().amount()), movementDirection(movement, participantEmail));
	}

	private static String movementDirection(Movement movement, Email participantEmail) {

		return switch (movement.type()) {
			case TRANSFER -> {
				if (participantEmail.equals(movement.to().getEmail())) {
					yield "IN";
				}
				yield "OUT";
			}
			case EXTERNAL_IN -> "EXTERNAL_LOAD";
		};
	}
}
