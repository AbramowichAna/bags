package edu.aseca.bags.application.dto;

import java.math.BigDecimal;

public record ExternalLoadRequest(String walletEmail, BigDecimal amount, String externalServiceName,
		String externalServiceType, String externalServiceEmail) {
}