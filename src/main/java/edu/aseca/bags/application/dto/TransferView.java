package edu.aseca.bags.application.dto;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.transaction.Transfer;

public record TransferView(String fromEmail, String toEmail, double amount, String timestamp, String transferNumber,
		TransferDirection direction) {
	public TransferView(Transfer transfer, Email ownerEmail) {
		this(transfer.fromWallet().getEmail().address(), transfer.toWallet().getEmail().address(),
				transfer.amount().amount(), transfer.timestamp().toString(),
				transfer.transferNumber().value().toString(), calculateDirection(transfer, ownerEmail));
	}

	private static TransferDirection calculateDirection(Transfer transfer, Email ownerEmail) {
		if (transfer.toWallet().getEmail().equals(ownerEmail)) {
			return TransferDirection.IN;
		} else {
			return TransferDirection.OUT;
		}
	}
}
