package edu.aseca.bags.application.interfaces;

import edu.aseca.bags.domain.transaction.TransferNumber;

public interface MovementIdGenerator {
	TransferNumber generate();
}
