package edu.aseca.bags.api;

import edu.aseca.bags.api.dto.DebInRequest;
import edu.aseca.bags.application.DebInUseCase;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.exception.UnsupportedExternalService;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.security.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debin")
public class DebInController {

	private final DebInUseCase debInUseCase;
	private final SecurityService securityService;

	public DebInController(DebInUseCase debInUseCase, SecurityService securityService) {
		this.debInUseCase = debInUseCase;
		this.securityService = securityService;
	}

	@PostMapping
	public ResponseEntity<?> requestDebIn(@RequestBody DebInRequest request) {

		String fromEmailAddress = securityService.getMail();

		try {
			debInUseCase.requestDebIn(new Email(fromEmailAddress), request.externalServiceName(),
					ServiceType.valueOf(request.serviceType()), request.externalEmail(), request.amount());
			return ResponseEntity.ok().build();
		} catch (WalletNotFoundException e) {
			return ResponseEntity.status(404).body("Wallet not found");
		} catch (UnsupportedExternalService e) {
			return ResponseEntity.status(400).body("Unsupported external service");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}