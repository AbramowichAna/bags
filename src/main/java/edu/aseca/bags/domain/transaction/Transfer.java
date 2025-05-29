package edu.aseca.bags.domain.transaction;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.wallet.Wallet;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Transfer(UUID transferNumber, Wallet fromWallet, Wallet toWallet, Money amount, Instant timestamp) {

	public Transfer(Wallet fromWallet, Wallet toWallet, Money amount, Instant timestamp) {
		this(UUID.randomUUID(), fromWallet, toWallet, amount, timestamp);
	}

	public Transfer {
		Objects.requireNonNull(transferNumber, "Transfer number must be defined");
		Objects.requireNonNull(fromWallet, "From wallet must be defined");
		Objects.requireNonNull(toWallet, "To wallet must be defined");
		Objects.requireNonNull(amount, "Amount must be defined");
		Objects.requireNonNull(timestamp, "Timestamp must be defined");
	}
}