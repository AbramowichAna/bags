package edu.aseca.bags.application.util;

import edu.aseca.bags.application.interfaces.MovementIdGenerator;
import edu.aseca.bags.domain.transaction.TransferNumber;

public class KnownMovementIdGenerator implements MovementIdGenerator {

	private final TransferNumber transferNumber;

	public KnownMovementIdGenerator(TransferNumber transferNumber) {
		this.transferNumber = transferNumber;
	}

	@Override
	public TransferNumber generate() {
		return transferNumber;
	}
}
