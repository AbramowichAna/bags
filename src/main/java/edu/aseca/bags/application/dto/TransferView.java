package edu.aseca.bags.application.dto;

import edu.aseca.bags.domain.transaction.Transfer;

public record TransferView(String fromEmail, String toEmail, Double amount, String timestamp, String transferNumber) {
	public TransferView(Transfer transfer) {
		this(transfer.fromWallet().getEmail().address(), transfer.toWallet().getEmail().address(),
				transfer.amount().amount(), transfer.timestamp().toString(), transfer.transferNumber().toString());
	}
}
