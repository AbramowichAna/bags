package edu.aseca.bags.api.dto;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class ExternalLoadRequest {
	private String walletEmail;
	private BigDecimal amount;
	private String externalService;
	private String externalTransactionId;

}