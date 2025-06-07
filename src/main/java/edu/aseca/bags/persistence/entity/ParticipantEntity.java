package edu.aseca.bags.persistence.entity;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.ServiceType;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "participant_type")
public abstract class ParticipantEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected UUID id;

	abstract String getServiceName();

	abstract ServiceType getServiceType();

	abstract Email getEmail();
}
