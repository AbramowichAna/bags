package edu.aseca.bags.domain.transaction;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.domain.participant.Wallet;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ExternalLoad(UUID transactionId, Wallet toWallet, Money amount, Instant timestamp,
		ExternalAccount externalAccount) implements Transaction {

	public ExternalLoad {
		Objects.requireNonNull(transactionId, "Transaction ID must be defined");
		Objects.requireNonNull(toWallet, "Wallet must be defined");
		Objects.requireNonNull(amount, "Amount must be defined");
		Objects.requireNonNull(timestamp, "Timestamp must be defined");
		Objects.requireNonNull(externalAccount, "External service must be defined");
	}

	@Override
	public UUID getId() {
		return transactionId;
	}

	@Override
	public double getAmount() {
		return amount.amount();
	}

	@Override
	public Participant getFrom() {
		return externalAccount;
	}

	@Override
	public Participant getTo() {
		return toWallet;
	}

	@Override
	public Instant getTimestamp() {
		return timestamp;
	}
}