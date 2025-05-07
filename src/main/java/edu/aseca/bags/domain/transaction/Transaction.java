package edu.aseca.bags.domain.transaction;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Participant;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	private Participant from;

	@ManyToOne
	private Participant to;

	@Embedded
	private Money amount;

	private Instant timestamp;

	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	public Transaction(Participant from, Participant to, Money amount, Instant timestamp,
			TransactionType transactionType) {
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.timestamp = timestamp;
		this.transactionType = transactionType;
	}
}