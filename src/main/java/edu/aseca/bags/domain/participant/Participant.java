package edu.aseca.bags.domain.participant;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Participant {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

}