package edu.aseca.bags.api;

import edu.aseca.bags.application.DebInUseCase;
import edu.aseca.bags.application.dto.DebInRequest;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.exception.UnsupportedExternalService;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.security.SecurityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debin")
@Validated
public class DebInController {

	private final DebInUseCase debInUseCase;
	private final SecurityService securityService;

	public DebInController(DebInUseCase debInUseCase, SecurityService securityService) {
		this.debInUseCase = debInUseCase;
		this.securityService = securityService;
	}

	@PostMapping
	public ResponseEntity<?> requestDebIn(@Valid @RequestBody DebInRequest request)
			throws WalletNotFoundException, UnsupportedExternalService {

		String fromEmailAddress = securityService.getMail();

		debInUseCase.requestDebIn(new Email(fromEmailAddress), request.externalServiceName(), request.serviceType(),
				new Email(request.externalEmail()), request.amount());

		return ResponseEntity.ok().build();
	}
}