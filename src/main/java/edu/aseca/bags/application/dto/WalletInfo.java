package edu.aseca.bags.application.dto;

import edu.aseca.bags.domain.money.Money;

public record WalletInfo(Money balance) {

	public WalletInfo {
		if (balance == null) {
			throw new IllegalArgumentException("Balance cannot be null");
		}
	}
}
