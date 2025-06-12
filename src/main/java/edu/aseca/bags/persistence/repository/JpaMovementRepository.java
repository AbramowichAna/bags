package edu.aseca.bags.persistence.repository;

import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.interfaces.MovementRepository;
import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.domain.transaction.Movement;
import edu.aseca.bags.domain.transaction.MovementId;
import edu.aseca.bags.persistence.entity.ParticipantEntity;
import edu.aseca.bags.persistence.mapper.MovementMapper;
import edu.aseca.bags.persistence.mapper.participantstrategy.ParticipantResolver;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class JpaMovementRepository implements MovementRepository {

	private final SpringMovementJpaRepository jpaRepository;
	private final MovementMapper mapper;
	private final ParticipantResolver participantResolver;

	public JpaMovementRepository(SpringMovementJpaRepository jpaRepository, MovementMapper mapper,
			ParticipantResolver participantResolver) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
		this.participantResolver = participantResolver;
	}

	@Override
	public void save(Movement movement) {
		ParticipantEntity from = participantResolver.resolve(movement.from());
		ParticipantEntity to = participantResolver.resolve(movement.to());

		jpaRepository.save(mapper.toEntity(movement, from, to));
	}

	@Override
	public Page<Movement> findByParticipant(Participant participant, Pagination page) {
		return jpaRepository.findAllByFromOrTo(participantResolver.resolve(participant),
				participantResolver.resolve(participant), PageRequest.of(page.page(), page.size(), Sort.by(Sort.Direction.DESC, "timestamp")))
				.map(mapper::toDomain);
	}

	@Override
	public Optional<Movement> findById(MovementId id) {
		return Optional.empty();
	}
}
