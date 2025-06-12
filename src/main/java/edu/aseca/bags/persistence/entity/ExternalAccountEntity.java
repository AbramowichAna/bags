package edu.aseca.bags.persistence.entity;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.ServiceType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("EXTERNAL_ACCOUNT")
@Table(name = "external_accounts")
@AllArgsConstructor
@NoArgsConstructor
public class ExternalAccountEntity extends ParticipantEntity {

	@Column(name = "service", nullable = false)
	private String serviceName;

	@Column(name = "service_type", nullable = false)
	private String serviceType;

	@Column(name = "service_email", nullable = false)
	private String email;

	@Override
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.valueOf(serviceType);
	}

	@Override
	public Email getEmail() {
		return new Email(email);
	}
}
