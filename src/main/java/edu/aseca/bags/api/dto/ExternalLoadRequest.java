package edu.aseca.bags.api.dto;

import java.math.BigDecimal;

public record ExternalLoadRequest(String walletEmail, BigDecimal amount, String externalService,
		String externalTransactionId) {
}