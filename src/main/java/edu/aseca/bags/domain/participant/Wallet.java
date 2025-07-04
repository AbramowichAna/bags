package edu.aseca.bags.domain.participant;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.exception.AlreadyLinkedExternalAccount;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class Wallet implements Participant {

	@Getter
	private final Email email;
	@Getter
	private final Password password;
	@Getter
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

	public boolean hasSufficientBalance(Money amount) {
		return this.balance.amount() >= amount.amount();
	}

	@Override
	public String getServiceName() {
		return "Bags";
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.VIRTUAL_WALLET;
	}
}