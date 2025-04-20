package edu.aseca.bags.money;

public record Money(double amount) {

	public Money {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount cannot be negative: " + amount);
		}
	}

	public static Money of(double amount) {
		return new Money(amount);
	}

	public Money add(Money money) {
		return new Money(amount + money.amount);
	}

	public Money subtract(Money money) {
		double result = amount - money.amount;

		if (result < 0) {
			throw new IllegalArgumentException("Resulting amount cannot be negative");
		}
		return new Money(result);
	}
}
