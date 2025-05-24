package edu.aseca.bags.domain.transaction;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.wallet.Wallet;
import java.time.Instant;
import java.util.Objects;

public record Transfer(Wallet fromWallet, Wallet toWallet, Money amount, Instant timestamp) {

	public Transfer {
		Objects.requireNonNull(fromWallet, "From wallet must be defined");
		Objects.requireNonNull(toWallet, "To wallet must be defined");
		Objects.requireNonNull(amount, "Amount must be defined");
		Objects.requireNonNull(timestamp, "Timestamp must be defined");
	}

}