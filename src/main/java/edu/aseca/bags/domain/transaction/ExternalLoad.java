package edu.aseca.bags.domain.transaction;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.wallet.Wallet;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ExternalLoad(UUID transactionId, Wallet toWallet, Money amount, Instant timestamp,
		ExternalService service) {

	public ExternalLoad {
		Objects.requireNonNull(transactionId, "Transaction ID must be defined");
		Objects.requireNonNull(toWallet, "Wallet must be defined");
		Objects.requireNonNull(amount, "Amount must be defined");
		Objects.requireNonNull(timestamp, "Timestamp must be defined");
		Objects.requireNonNull(service, "External service must be defined");
	}
}