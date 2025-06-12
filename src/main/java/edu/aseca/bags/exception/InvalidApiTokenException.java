package edu.aseca.bags.exception;

public class InvalidApiTokenException extends RuntimeException {
	public InvalidApiTokenException(String message) {
		super(message);
	}
}