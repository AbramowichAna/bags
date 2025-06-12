package edu.aseca.bags.persistence.entity;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.ServiceType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "wallets")
@DiscriminatorValue("WALLET")
public class WalletEntity extends ParticipantEntity {

	private String email;

	@Setter
	@Getter
	private String password;

	@Getter
	@Setter
	private BigDecimal balance;

	protected WalletEntity() {
	}

	public WalletEntity(String email, String password, BigDecimal balance) {
		this.email = email;
		this.password = password;
		this.balance = balance;
	}

	@Override
	public String getServiceName() {
		return "Bags";
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.VIRTUAL_WALLET;
	}

	@Override
	public Email getEmail() {
		return new Email(email);
	}
}
