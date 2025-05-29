package edu.aseca.bags.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "transfers")
public class TransferEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	UUID id;

	@Column(nullable = false, unique = true)
	private UUID transferNumber;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "from_wallet_id")
	private WalletEntity fromWallet;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "to_wallet_id")
	private WalletEntity toWallet;

	@Column(nullable = false)
	private Double amount;

	@Column(nullable = false)
	private Instant timestamp;

	public TransferEntity() {
	}

	public TransferEntity(WalletEntity fromWallet, WalletEntity toWallet, Double amount, Instant timestamp) {
		this.transferNumber = UUID.randomUUID();
		this.fromWallet = fromWallet;
		this.toWallet = toWallet;
		this.amount = amount;
		this.timestamp = timestamp;
	}

}