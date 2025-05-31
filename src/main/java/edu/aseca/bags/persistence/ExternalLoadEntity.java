package edu.aseca.bags.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "external_loads")
public class ExternalLoadEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column(name = "load_id", nullable = false, updatable = false)
	private UUID transactionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wallet_id", nullable = false)
	private WalletEntity toWallet;

	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	@Column(name = "timestamp", nullable = false)
	private Instant timestamp;

	@Column(name = "service", nullable = false)
	private String service;

	public ExternalLoadEntity() {
	}

	public ExternalLoadEntity(UUID transactionId, WalletEntity toWallet, BigDecimal amount, Instant timestamp,
			String service) {
		this.transactionId = transactionId;
		this.toWallet = toWallet;
		this.amount = amount;
		this.timestamp = timestamp;
		this.service = service;
	}
}