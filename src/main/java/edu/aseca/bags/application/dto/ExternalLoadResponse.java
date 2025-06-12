package edu.aseca.bags.application.dto;

import java.time.Instant;

public record ExternalLoadResponse(String walletEmail, double amount, String externalServiceName,
		String externalServiceType, String externalServiceEmail, String externalTransactionId, Instant timestamp,
		String status) {
}