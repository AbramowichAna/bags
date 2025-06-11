package edu.aseca.bags.exception;

public class ExternalEntityNotFoundException extends RuntimeException {

	private static final String message = "External entity not found";

	public ExternalEntityNotFoundException() {
		super(message);
	}

	public ExternalEntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExternalEntityNotFoundException(Throwable cause) {
		super(cause);
	}
}
