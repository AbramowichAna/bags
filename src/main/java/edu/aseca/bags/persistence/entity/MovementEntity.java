package edu.aseca.bags.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "movements")
@NoArgsConstructor
public class MovementEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column(nullable = false, unique = true)
	private String transferNumber;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "from_id")
	private ParticipantEntity from;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "to_id")
	private ParticipantEntity to;

	@Column(nullable = false)
	private Instant timestamp;

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(nullable = false)
	private String type;

	public MovementEntity(String transferNumber, ParticipantEntity from, ParticipantEntity to, Instant timestamp,
			BigDecimal amount, String type) {
		this.transferNumber = transferNumber;
		this.from = from;
		this.to = to;
		this.timestamp = timestamp;
		this.amount = amount;
		this.type = type;
	}
}
