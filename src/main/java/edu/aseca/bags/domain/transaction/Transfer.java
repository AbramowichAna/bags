package edu.aseca.bags.domain.transaction;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.domain.participant.Wallet;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Transfer(TransferNumber transferNumber, Wallet fromWallet, Wallet toWallet, Money amount,
		Instant timestamp) implements Transaction {

	public Transfer(Wallet fromWallet, Wallet toWallet, Money amount, Instant timestamp) {
		this(TransferNumber.random(), fromWallet, toWallet, amount, timestamp);
	}

	public Transfer {
		Objects.requireNonNull(transferNumber, "Transfer number must be defined");
		Objects.requireNonNull(fromWallet, "From wallet must be defined");
		Objects.requireNonNull(toWallet, "To wallet must be defined");
		Objects.requireNonNull(amount, "Amount must be defined");
		Objects.requireNonNull(timestamp, "Timestamp must be defined");
	}

	@Override
	public UUID getId() {
		return transferNumber.value();
	}

	@Override
	public double getAmount() {
		return amount.amount();
	}

	@Override
	public Participant getFrom() {
		return fromWallet;
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
