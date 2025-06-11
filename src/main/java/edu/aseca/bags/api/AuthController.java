package edu.aseca.bags.api;

import edu.aseca.bags.application.AuthService;
import edu.aseca.bags.application.CreateWalletUseCase;
import edu.aseca.bags.application.dto.AuthRequest;
import edu.aseca.bags.application.dto.AuthResponse;
import edu.aseca.bags.application.dto.CreateWalletRequest;
import edu.aseca.bags.exception.AlreadyExistingWallet;
import edu.aseca.bags.exception.WalletNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

	private final CreateWalletUseCase createWalletUseCase;
	private final AuthService service;

	public AuthController(CreateWalletUseCase createWalletUseCase, AuthService service) {
		this.createWalletUseCase = createWalletUseCase;
		this.service = service;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody @Valid CreateWalletRequest request) throws AlreadyExistingWallet {
		createWalletUseCase.create(request.email(), request.password());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) throws WalletNotFoundException {
		return ResponseEntity.ok(service.login(request));
	}
}
