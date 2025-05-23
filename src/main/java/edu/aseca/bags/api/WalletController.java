package edu.aseca.bags.api;

import edu.aseca.bags.application.WalletQuery;
import edu.aseca.bags.application.dto.WalletInfo;
import edu.aseca.bags.exception.BadPermissionException;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.security.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
@Validated
public class WalletController {

	private final WalletQuery walletQuery;
	private final SecurityService securityService;

	public WalletController(WalletQuery walletQuery, SecurityService securityService) {
		this.walletQuery = walletQuery;
		this.securityService = securityService;
	}

	@GetMapping("")
	public ResponseEntity<WalletInfo> getWalletInfo() throws WalletNotFoundException, BadPermissionException {
		String mail = securityService.getMail();
		return ResponseEntity.ok(walletQuery.getWalletInfoOf(mail, mail));
	}

}
