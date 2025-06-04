package edu.aseca.bags.domain.transaction;

import edu.aseca.bags.domain.participant.Participant;
import java.time.Instant;
import java.util.UUID;

public interface Transaction {
	UUID getId();

	double getAmount();

	Participant getFrom();

	Participant getTo();

	Instant getTimestamp();
}
