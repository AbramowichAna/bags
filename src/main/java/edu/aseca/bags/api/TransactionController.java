package edu.aseca.bags.api;

import edu.aseca.bags.application.TransactionQuery;
import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.dto.TransactionView;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.security.SecurityService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
@Validated
public class TransactionController {

	private final TransactionQuery transactionQuery;
	private final SecurityService securityService;

	public TransactionController(TransactionQuery transactionQuery, SecurityService securityService) {
		this.transactionQuery = transactionQuery;
		this.securityService = securityService;
	}

	@GetMapping
	public ResponseEntity<Page<TransactionView>> getTransactions(@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Positive int size) throws WalletNotFoundException {
		String email = securityService.getMail();
		List<TransactionView> transactions = transactionQuery.getTransactions(new Email(email),
				new Pagination(page, size));
		Page<TransactionView> responsePage = new PageImpl<>(transactions, PageRequest.of(page, size),
				transactions.size());
		return ResponseEntity.ok(responsePage);
	}
}