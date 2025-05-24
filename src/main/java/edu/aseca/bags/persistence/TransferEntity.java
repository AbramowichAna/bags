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
	private String id;

	@ManyToOne
	@JoinColumn(name = "from_wallet_id")
	private WalletEntity fromWallet;

	@ManyToOne
	@JoinColumn(name = "to_wallet_id")
	private WalletEntity toWallet;

	private Double amount;

	private Instant timestamp;

	public TransferEntity() {
		this.id = UUID.randomUUID().toString();
	}

	public TransferEntity(WalletEntity fromWallet, WalletEntity toWallet, Double amount, Instant timestamp) {
		this();
		this.fromWallet = fromWallet;
		this.toWallet = toWallet;
		this.amount = amount;
		this.timestamp = timestamp;
	}

}