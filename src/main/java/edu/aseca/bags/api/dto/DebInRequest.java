package edu.aseca.bags.api.dto;

public record DebInRequest(String externalServiceName, String serviceType, String externalEmail, double amount) {
}