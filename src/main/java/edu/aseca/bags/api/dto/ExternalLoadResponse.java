package edu.aseca.bags.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExternalLoadResponse {
	private String walletEmail;
	private BigDecimal amount;
	private String externalService;
	private String externalTransactionId;
	private Instant timestamp;
	private String status;

	public ExternalLoadResponse(String walletEmail, BigDecimal amount, String externalService,
			String externalTransactionId, Instant timestamp, String status) {
		this.walletEmail = walletEmail;
		this.amount = amount;
		this.externalService = externalService;
		this.externalTransactionId = externalTransactionId;
		this.timestamp = timestamp;
		this.status = status;
	}

}