package edu.aseca.bags.domain.bank;

import edu.aseca.bags.domain.participant.Participant;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class ExternalAccount extends Participant {

	private String name;

	public ExternalAccount(String name) {
		this.name = name;
	}

}
