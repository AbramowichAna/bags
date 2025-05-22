package edu.aseca.bags.domain.money;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.aseca.bags.domain.money.Money;
import org.junit.jupiter.api.Test;

class MoneyTest {

	@Test
	void shouldCreateMoneyWithGivenAmount() {
		Money money = new Money(10.5);
		assertEquals(10.5, money.amount());
	}

	@Test
	void shouldCreateMoneyUsingFactoryMethod() {
		Money money = Money.of(7.25);
		assertEquals(7.25, money.amount());
	}

	@Test
	void shouldAddTwoMoneyInstancesCorrectly() {
		Money money1 = new Money(5.0);
		Money money2 = new Money(3.5);
		Money result = money1.add(money2);
		assertEquals(8.5, result.amount());
	}

	@Test
	void shouldSubtractTwoMoneyInstancesCorrectly() {
		Money money1 = new Money(10.0);
		Money money2 = new Money(4.5);
		Money result = money1.subtract(money2);
		assertEquals(5.5, result.amount());
	}

	@Test
	void shouldNotAllowCreatingMoneyWithNegativeAmount() {
		assertThrows(IllegalArgumentException.class, () -> new Money(-2.0));
	}

	@Test
	void shouldNotAllowSubtractionThatResultsInNegativeAmount() {
		Money money1 = new Money(3.0);
		Money money2 = new Money(5.0);
		assertThrows(IllegalArgumentException.class, () -> money1.subtract(money2));
	}

	@Test
	void shouldHandleAdditionWithZero() {
		Money money = new Money(9.0);
		Money result = money.add(Money.of(0));
		assertEquals(9.0, result.amount());
	}

	@Test
	void shouldHandleSubtractionWithZero() {
		Money money = new Money(9.0);
		Money result = money.subtract(Money.of(0));
		assertEquals(9.0, result.amount());
	}
}
