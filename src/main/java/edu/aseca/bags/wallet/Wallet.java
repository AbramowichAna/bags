package edu.aseca.bags.wallet;

import edu.aseca.bags.email.Email;
import edu.aseca.bags.email.Password;
import edu.aseca.bags.money.Money;

public class Wallet {

	private final Email email;
	private final Password password;
	private Money balance;

	public Wallet(Email email, Password password) {
		this.email = email;
		this.password = password;
		this.balance = new Money(0);
	}

	public void addBalance(Money balance) {
		this.balance = this.balance.add(balance);
	}

	public void subtractBalance(Money balance) {
		try {
			this.balance = this.balance.subtract(balance);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Balance not sufficient", e);
		}
	}

	public Money getBalance() {
		return this.balance;
	}

}