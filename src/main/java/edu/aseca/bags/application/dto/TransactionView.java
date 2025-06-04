package edu.aseca.bags.application.dto;

import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.domain.transaction.Transaction;
import java.time.Instant;
import java.util.UUID;

public record TransactionView(UUID id, double amount, String fromServiceName, String fromServiceType, String fromEmail,
		String toServiceName, String toServiceType, String toEmail, Instant timestamp, String type) {
	public static TransactionView from(Transaction tx, String type) {
		Participant from = tx.getFrom();
		Participant to = tx.getTo();
		return new TransactionView(tx.getId(), tx.getAmount(), from.getServiceName(), from.getServiceType().name(),
				from.getEmail().address(), to.getServiceName(), to.getServiceType().name(), to.getEmail().address(),
				tx.getTimestamp(), type);
	}
}