package edu.aseca.bags.exception;

public class InsufficientFundsException extends Exception {

	public static String MESSAGE = "Insufficient funds for transfer";

	public InsufficientFundsException() {
		super(MESSAGE);
	}
}