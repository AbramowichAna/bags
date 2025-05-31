package edu.aseca.bags.api;

import edu.aseca.bags.api.dto.ExternalLoadRequest;
import edu.aseca.bags.api.dto.ExternalLoadResponse;
import edu.aseca.bags.application.ExternalLoadUseCase;
import edu.aseca.bags.exception.WalletNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external-load")
@Validated
public class ExternalLoadController {

	private final ExternalLoadUseCase externalLoadUseCase;

	public ExternalLoadController(ExternalLoadUseCase externalLoadUseCase) {
		this.externalLoadUseCase = externalLoadUseCase;
	}

	@PostMapping
	public ResponseEntity<ExternalLoadResponse> externalLoad(@Valid @RequestBody ExternalLoadRequest request)
			throws WalletNotFoundException {
		ExternalLoadResponse response = externalLoadUseCase.loadFromExternal(request);
		return ResponseEntity.ok(response);
	}

}
