package edu.aseca.bags.exception;

public class WalletNotFoundException extends Exception {

	public static String MESSAGE = "Wallet not found";

	public WalletNotFoundException() {
		super(MESSAGE);
	}
}