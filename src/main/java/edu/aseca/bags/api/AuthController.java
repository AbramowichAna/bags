package edu.aseca.bags.api;

import edu.aseca.bags.application.CreateWalletUseCase;
import edu.aseca.bags.exception.AlreadyExistingWallet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final CreateWalletUseCase createWalletUseCase;

	public AuthController(CreateWalletUseCase createWalletUseCase) {
		this.createWalletUseCase = createWalletUseCase;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody CreateWalletRequest request) throws AlreadyExistingWallet {
		createWalletUseCase.create(request.email(), request.password());
		return ResponseEntity.ok().build();
	}
}
