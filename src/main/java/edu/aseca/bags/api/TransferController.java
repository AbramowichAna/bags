package edu.aseca.bags.api;

import edu.aseca.bags.application.TransferUseCase;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.exception.InsufficientFundsException;
import edu.aseca.bags.exception.WalletNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer")
@Validated
public class TransferController {

	private final TransferUseCase transferUseCase;

	public TransferController(TransferUseCase transferUseCase) {
		this.transferUseCase = transferUseCase;
	}

	@PostMapping
	public ResponseEntity<TransferResponse> transfer(@RequestBody @Valid TransferRequest request)
			throws WalletNotFoundException, InsufficientFundsException {
		Transfer transfer = transferUseCase.execute(new Email(request.fromEmail()), new Email(request.toEmail()),
				new Money(request.amount()));
		return ResponseEntity.ok(new TransferResponse(transfer));
	}

	public record TransferRequest(@NotNull String fromEmail, @NotNull String toEmail, @NotNull Double amount) {
	}

	public record TransferResponse(String fromEmail, String toEmail, Double amount, String timestamp) {
		public TransferResponse(Transfer transfer) {
			this(transfer.fromWallet().getEmail().address(), transfer.toWallet().getEmail().address(),
					transfer.amount().amount(), transfer.timestamp().toString());
		}
	}
}