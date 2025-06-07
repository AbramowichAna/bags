package edu.aseca.bags.application.queries;

import edu.aseca.bags.application.dto.MovementView;
import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.interfaces.MovementRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.Participant;
import org.springframework.data.domain.Page;

public class MovementQuery {

	private final MovementRepository movementRepository;

	public MovementQuery(MovementRepository movementRepository) {
		this.movementRepository = movementRepository;
	}

	public Page<MovementView> getMovements(Participant participant, Pagination page) {
		return movementRepository.findByParticipant(participant, page)
				.map(movement -> MovementView.from(movement, participant.getEmail()));
	}

}
