package edu.aseca.bags.application.interfaces;

import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.domain.transaction.Movement;
import edu.aseca.bags.domain.transaction.TransferNumber;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface MovementRepository {
	void save(Movement movement);

	Page<Movement> findByParticipant(Participant participant, Pagination page);

	Optional<Movement> findById(TransferNumber id);
}
