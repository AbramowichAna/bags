package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.TransferNumber;

public class KnownTransferNumberGenerator implements TransferNumberGenerator {

	private final TransferNumber transferNumber;

	public KnownTransferNumberGenerator(TransferNumber transferNumber) {
		this.transferNumber = transferNumber;
	}

	@Override
	public TransferNumber generate() {
		return transferNumber;
	}
}
