package edu.aseca.bags.exception;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.*;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	// Not developing correctly would get here
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest req) {
		// Change for explicit logging
		ex.printStackTrace(); // Log the exception stack trace for debugging
		return buildErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, "Error",
				req.getRequestURI(), null);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpServletRequest req) {
		return buildErrorResponse("Bad Request", HttpStatus.BAD_REQUEST, "Malformed JSON request", req.getRequestURI(),
				null);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)

	public ResponseEntity<ApiErrorResponse> handleHttpRequestMethodNotSupported(
			HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
		String message = String.format("Method '%s' is not supported for this request. Supported methods are: %s",
				ex.getMethod(), ex.getSupportedHttpMethods());
		return buildErrorResponse("Method Not Allowed", HttpStatus.METHOD_NOT_ALLOWED, message, req.getRequestURI(),
				null);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
			HttpServletRequest req) {
		String paramName = ex.getName();
		String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
		String receivedValue = ex.getValue() != null ? ex.getValue().toString() : "null";
		String message = String.format("Parameter '%s' must be of type '%s', but received: '%s'", paramName,
				expectedType, receivedValue);

		return buildErrorResponse("Bad Request", HttpStatus.BAD_REQUEST, message, req.getRequestURI(), null);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ApiErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
			HttpServletRequest req) {
		String message = String.format("Media type '%s' is not supported. Supported media types are: %s",
				ex.getContentType(), ex.getSupportedMediaTypes());
		return buildErrorResponse("Unsupported Media Type", HttpStatus.UNSUPPORTED_MEDIA_TYPE, message,
				req.getRequestURI(), null);
	}

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

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest request) {
		Map<String, String> validationErrors = ex.getConstraintViolations().stream().collect(Collectors.toMap(v -> {
			String path = v.getPropertyPath().toString();
			return path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
		}, ConstraintViolation::getMessage, (existing, replacement) -> existing));

		return buildErrorResponse("Validation error", HttpStatus.BAD_REQUEST, "Some parameters are invalid.",
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

	@ExceptionHandler(BadPermissionException.class)
	public ResponseEntity<ApiErrorResponse> handleBadPermissions(BadPermissionException ex,
			HttpServletRequest request) {
		return buildErrorResponse("You do not have permissions to this resource", HttpStatus.FORBIDDEN, ex.getMessage(),
				request.getRequestURI(), null);
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ApiErrorResponse> handleSignatureException(JwtException ex, HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(authError(req, ""));
	}

	@ExceptionHandler(InsufficientFundsException.class)
	public ResponseEntity<?> handleInsufficientFundsException(InsufficientFundsException e,
			HttpServletRequest request) {
		return buildErrorResponse("Insufficient funds", HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI(),
				null);
	}

	@ExceptionHandler(WalletNotFoundException.class)
	public ResponseEntity<?> handleWalletNotFoundException(WalletNotFoundException e, HttpServletRequest request) {
		return buildErrorResponse("Wallet not found", HttpStatus.NOT_FOUND, e.getMessage(), request.getRequestURI(),
				null);
	}

	@ExceptionHandler(InvalidTransferException.class)
	public ResponseEntity<?> handleInvalidTransferException(InvalidTransferException e, HttpServletRequest request) {
		return buildErrorResponse("Invalid transfer", HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI(),
				null);
	}

	@ExceptionHandler(edu.aseca.bags.exception.InvalidApiTokenException.class)
	public ResponseEntity<?> handleInvalidApiTokenException(edu.aseca.bags.exception.InvalidApiTokenException ex,
			HttpServletRequest request) {
		return ResponseEntity.status(401)
				.body(new ApiErrorResponse("Unauthorized", 401, ex.getMessage(), request.getRequestURI(), null));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(NoResourceFoundException ex, HttpServletRequest request) {
		ApiErrorResponse error = new ApiErrorResponse("Not Found", HttpStatus.NOT_FOUND.value(),
				"The requested resource was not found", request.getRequestURI(), Map.of());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
		ApiErrorResponse error = new ApiErrorResponse("Not Found", HttpStatus.NOT_FOUND.value(),
				"The requested resource was not found", request.getRequestURI(), Map.of());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	public record ApiErrorResponse(String title, int status, String detail, String instance,
			Map<String, String> errors) {
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

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.ENGLISH);
		return slr;
	}

}
