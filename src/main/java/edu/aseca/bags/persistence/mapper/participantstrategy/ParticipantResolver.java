package edu.aseca.bags.persistence.mapper.participantstrategy;

import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.persistence.entity.ParticipantEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ParticipantResolver {

	private final List<ParticipantEntityResolverStrategy> strategies;

	public ParticipantResolver(List<ParticipantEntityResolverStrategy> strategies) {
		this.strategies = strategies;
	}

	public ParticipantEntity resolve(Participant participant) {
		return strategies.stream().filter(strategy -> strategy.supports(participant)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"No resolver found for participant type: " + participant.getClass().getSimpleName()))
				.resolve(participant);
	}
}
