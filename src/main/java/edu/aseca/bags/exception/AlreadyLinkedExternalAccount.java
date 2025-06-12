package edu.aseca.bags.exception;

public class AlreadyLinkedExternalAccount extends RuntimeException {

	private static final String MESSAGE = "External account already linked";

	public AlreadyLinkedExternalAccount() {
		super(MESSAGE);
	}
}
