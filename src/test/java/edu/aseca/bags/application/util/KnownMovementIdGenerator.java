package edu.aseca.bags.application.util;

import edu.aseca.bags.application.interfaces.MovementIdGenerator;
import edu.aseca.bags.domain.transaction.MovementId;

public class KnownMovementIdGenerator implements MovementIdGenerator {

	private final MovementId movementId;

	public KnownMovementIdGenerator(MovementId movementId) {
		this.movementId = movementId;
	}

	@Override
	public MovementId generate() {
		return movementId;
	}
}
