package edu.aseca.bags.persistence.repository;

import edu.aseca.bags.persistence.entity.ParticipantEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringParticipantJpaRepository extends JpaRepository<ParticipantEntity, UUID> {

}
