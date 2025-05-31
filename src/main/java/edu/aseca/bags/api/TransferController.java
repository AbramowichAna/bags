package edu.aseca.bags.api;

import edu.aseca.bags.application.TransferQuery;
import edu.aseca.bags.application.TransferUseCase;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.exception.InsufficientFundsException;
import edu.aseca.bags.exception.InvalidTransferException;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.security.SecurityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
	private final TransferQuery transferQuery;

	public TransferController(TransferUseCase transferUseCase, SecurityService securityService,
			TransferQuery transferQuery) {
		this.transferUseCase = transferUseCase;
		this.securityService = securityService;
		this.transferQuery = transferQuery;
	}

	@Transactional
	@PostMapping
	public ResponseEntity<TransferResponse> transfer(@RequestBody @Valid TransferRequest request)
			throws WalletNotFoundException, InsufficientFundsException, InvalidTransferException {

		String fromEmail = securityService.getMail();

		Transfer transfer = transferUseCase.execute(new Email(fromEmail), new Email(request.toEmail()),
				new Money(request.amount()));

		return ResponseEntity.ok(new TransferResponse(transfer));
	}

	@GetMapping
	public ResponseEntity<Page<TransferResponse>> getTransfers(@RequestParam(defaultValue = "0") @Min(0) int page,
															   @RequestParam(defaultValue = "10") @Positive int size) throws WalletNotFoundException {
		String email = securityService.getMail();
		List<Transfer> transfers = transferQuery.getTransfers(new Email(email), page, size);
		List<TransferResponse> responses = transfers.stream().map(TransferResponse::new).toList();
		Page<TransferResponse> responsePage = new PageImpl<>(responses, PageRequest.of(page, size), responses.size());
		return ResponseEntity.ok(responsePage);
	}

	public record TransferRequest(@NotNull String toEmail, @NotNull Double amount) {
	}

	public record TransferResponse(String fromEmail, String toEmail, Double amount, String timestamp,
			String transferNumber) {
		public TransferResponse(Transfer transfer) {
			this(transfer.fromWallet().getEmail().address(), transfer.toWallet().getEmail().address(),
					transfer.amount().amount(), transfer.timestamp().toString(), transfer.transferNumber().toString());
		}
	}
}