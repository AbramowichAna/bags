package edu.aseca.bags.api.dto;

import java.time.Instant;

public record ExternalLoadResponse(String walletEmail, double amount, String externalService,
		String externalTransactionId, Instant timestamp, String status) {
}