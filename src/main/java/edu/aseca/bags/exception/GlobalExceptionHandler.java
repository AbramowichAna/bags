package edu.aseca.bags.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(AlreadyExistingWallet.class)
	public ResponseEntity<?> handleAlreadyExistingWallet(AlreadyExistingWallet e) {
		return ResponseEntity.status(409).build();
	}

	@ExceptionHandler(InsufficientFundsException.class)
	public ResponseEntity<?> handleInsufficientFundsException(InsufficientFundsException e) {
		return ResponseEntity.status(400).build();
	}

	@ExceptionHandler(WalletNotFoundException.class)
	public ResponseEntity<?> handleWalletNotFoundException(WalletNotFoundException e) {
		return ResponseEntity.status(404).build();
	}
}