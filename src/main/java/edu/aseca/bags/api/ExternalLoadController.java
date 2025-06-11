package edu.aseca.bags.api;

import edu.aseca.bags.application.ExternalLoadUseCase;
import edu.aseca.bags.application.dto.ExternalLoadRequest;
import edu.aseca.bags.application.dto.MovementView;
import edu.aseca.bags.exception.InvalidApiTokenException;
import edu.aseca.bags.exception.WalletNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/external-load")
@Validated
public class ExternalLoadController {

	private final ExternalLoadUseCase externalLoadUseCase;
	private final String apiToken;

	public ExternalLoadController(ExternalLoadUseCase externalLoadUseCase,
			@Value("${external.api.token}") String apiToken) {
		this.externalLoadUseCase = externalLoadUseCase;
		this.apiToken = apiToken;
	}

	@PostMapping
	public ResponseEntity<MovementView> externalLoad(@Valid @RequestBody ExternalLoadRequest request,
			@RequestHeader(value = "X-API-TOKEN", required = false) String token) throws WalletNotFoundException {
		if (!apiToken.equals(token)) {
			throw new InvalidApiTokenException("Invalid API token");
		}
		MovementView response = externalLoadUseCase.loadFromExternal(request);
		return ResponseEntity.ok(response);
	}
}