package edu.aseca.bags.domain.wallet;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.domain.transaction.Transaction;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Wallet extends Participant {

	@Embedded
	private Email email;

	@Embedded
	private Password password;

	@Getter
	@Embedded
	private Money balance;

	@OneToMany(mappedBy = "from")
	private final Set<Transaction> sentTransactions = new HashSet<>();

	@OneToMany(mappedBy = "to")
	private final Set<Transaction> receivedTransactions = new HashSet<>();

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

	public void addSentTransaction(Transaction transaction) {
		this.sentTransactions.add(transaction);
	}

	public void addReceivedTransaction(Transaction transaction) {
		this.receivedTransactions.add(transaction);
	}
}