package edu.aseca.bags.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintDeclarationException;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ConstraintDeclarationException.class)
	public ResponseEntity<ApiErrorResponse> handleConstraintDeclaration(ConstraintDeclarationException ex,
			HttpServletRequest req) {
		return buildErrorResponse("Bad Request", HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		Map<String, String> validationErrors = ex.getBindingResult().getFieldErrors().stream().collect(Collectors
				.toMap(FieldError::getField, FieldError::getDefaultMessage, (existing, replacement) -> existing));

		return buildErrorResponse("Validation error", HttpStatus.BAD_REQUEST, "Some fields are invalid.",
				request.getRequestURI(), validationErrors);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
		return ResponseEntity.status(401).body(authError(request, "Invalid email or password"));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<?> handleUserNotFound(UsernameNotFoundException ex, HttpServletRequest request) {
		return ResponseEntity.status(401).body(authError(request, ""));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
			HttpServletRequest req) {
		return buildErrorResponse("Bad Request", HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), null);
	}

	@ExceptionHandler(AlreadyExistingWallet.class)
	public ResponseEntity<ApiErrorResponse> handleWalletConflict(AlreadyExistingWallet ex, HttpServletRequest request) {
		return buildErrorResponse("Wallet already exists", HttpStatus.CONFLICT, ex.getMessage(),
				request.getRequestURI(), null);
	}

	public record ApiErrorResponse(String title, int status, String detail, String instance,
			Map<String, String> errors) {
	}

	@ExceptionHandler(InsufficientFundsException.class)
	public ResponseEntity<?> handleInsufficientFundsException(InsufficientFundsException e) {
		return ResponseEntity.status(400).build();
	}

	private static ApiErrorResponse authError(HttpServletRequest request, String message) {
		return new ApiErrorResponse("Unauthorized", HttpStatus.UNAUTHORIZED.value(), message, request.getRequestURI(),
				null);
	}

	private ResponseEntity<ApiErrorResponse> buildErrorResponse(String title, HttpStatus status, String detail,
			String instance, Map<String, String> errors) {
		ApiErrorResponse response = new ApiErrorResponse(title, status.value(), detail, instance, errors);
		return ResponseEntity.status(status).body(response);
	}

}

	@ExceptionHandler(WalletNotFoundException.class)
	public ResponseEntity<?> handleWalletNotFoundException(WalletNotFoundException e) {
		return ResponseEntity.status(404).build();
	}
}