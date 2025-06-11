package edu.aseca.bags.application.interfaces;

import edu.aseca.bags.domain.transaction.MovementId;

public interface MovementIdGenerator {
	MovementId generate();
}
