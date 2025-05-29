package edu.aseca.bags.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "wallets")
public class WalletEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;

	private String password;

	@Setter
	private BigDecimal balance;

	protected WalletEntity() {
	}

	public WalletEntity(String email, String password, BigDecimal balance) {
		this.email = email;
		this.password = password;
		this.balance = balance;
	}
}
