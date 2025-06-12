package edu.aseca.bags.persistence.repository;

import edu.aseca.bags.persistence.entity.MovementEntity;
import edu.aseca.bags.persistence.entity.ParticipantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringMovementJpaRepository extends JpaRepository<MovementEntity, Long> {

	Page<MovementEntity> findAllByFromOrTo(ParticipantEntity from, ParticipantEntity to, Pageable pageable);
}
