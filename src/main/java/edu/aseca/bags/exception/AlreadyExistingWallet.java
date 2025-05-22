package edu.aseca.bags.exception;

public class AlreadyExistingWallet extends Exception {

	public static String MESSAGE = "Wallet with that email already exists";

	public AlreadyExistingWallet() {
		super(MESSAGE);
	}
}
