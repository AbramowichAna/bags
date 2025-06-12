package edu.aseca.bags.persistence.mapper.participantstrategy;

import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.persistence.entity.ParticipantEntity;

public interface ParticipantEntityResolverStrategy {
	boolean supports(Participant participant);

	ParticipantEntity resolve(Participant participant);
}
