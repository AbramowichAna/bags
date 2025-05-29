package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.TransferNumber;

public interface TransferNumberGenerator {
	TransferNumber generate();
}
