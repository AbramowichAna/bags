package edu.aseca.bags.api;

import edu.aseca.bags.application.TransferUseCase;
import edu.aseca.bags.application.dto.MovementView;
import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.queries.WalletMovementsQuery;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.exception.InsufficientFundsException;
import edu.aseca.bags.exception.InvalidTransferException;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.security.SecurityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
@Validated
public class TransferController {

	private final TransferUseCase transferUseCase;
	private final SecurityService securityService;
	private final WalletMovementsQuery movementsQuery;

	public TransferController(TransferUseCase transferUseCase, SecurityService securityService,
			WalletMovementsQuery movementsQuery) {
		this.transferUseCase = transferUseCase;
		this.securityService = securityService;
		this.movementsQuery = movementsQuery;
	}

	@Transactional
	@PostMapping
	public ResponseEntity<MovementView> transfer(@RequestBody @Valid TransferRequest request)
			throws WalletNotFoundException, InsufficientFundsException, InvalidTransferException {

		String fromEmailAddress = securityService.getMail();

		Email fromEmail = new Email(fromEmailAddress);
		MovementView transfer = MovementView.from(
				transferUseCase.execute(fromEmail, new Email(request.toEmail()), new Money(request.amount())),
				fromEmail);

		return ResponseEntity.ok(transfer);
	}

	@GetMapping
	public ResponseEntity<Page<MovementView>> getTransfers(@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Positive int size) throws WalletNotFoundException {
		String email = securityService.getMail();
		var responses = movementsQuery.getMovements(new Email(email), new Pagination(page, size));
		return ResponseEntity.ok(responses);
	}

	public record TransferRequest(@NotNull String toEmail, @NotNull Double amount) {
	}
}