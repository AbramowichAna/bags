package edu.aseca.bags.exception;

public class UnsupportedExternalService extends Exception {

	private static final String DEFAULT_MESSAGE = "Unsupported external service";

	public UnsupportedExternalService(String service) {
		super(DEFAULT_MESSAGE + ": " + service);
	}
}
