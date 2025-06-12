package edu.aseca.bags.application.util;

import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.interfaces.MovementRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.domain.transaction.Movement;
import edu.aseca.bags.domain.transaction.MovementId;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class InMemoryMovementRepository implements MovementRepository {
	private final Map<MovementId, Movement> data = new HashMap<>();

	@Override
	public void save(Movement movement) {
		data.put(movement.movementId(), movement);
	}

	@Override
	public Page<Movement> findByParticipant(Participant participant, Pagination page) {
		Email participantEmail = participant.getEmail();
		List<Movement> filteredMovements = data.values().stream()
				.filter(movement -> movement.from().getEmail().equals(participantEmail)
						|| movement.to().getEmail().equals(participantEmail))
				.sorted(Comparator.comparing(Movement::timestamp).reversed()).collect(Collectors.toList());

		int fromIndex = page.page() * page.size();
		int toIndex = Math.min(fromIndex + page.size(), filteredMovements.size());
		if (fromIndex > toIndex) {
			return new PageImpl<>(Collections.emptyList(), PageRequest.of(page.page(), page.size()),
					filteredMovements.size());
		}

		return new PageImpl<>(filteredMovements.subList(fromIndex, toIndex), PageRequest.of(page.page(), page.size()),
				filteredMovements.size());
	}

	@Override
	public Optional<Movement> findById(MovementId id) {
		return Optional.ofNullable(data.get(id));
	}

	public Optional<Movement> findByTransferNumber(MovementId movementId) {
		return Optional.ofNullable(data.get(movementId));
	}

	public int count() {
		return data.size();
	}

	public void clear() {
		data.clear();
	}
}